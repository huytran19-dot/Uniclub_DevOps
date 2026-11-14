package com.uniclub.service.impl;

import com.uniclub.service.StorageService;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Service
public class R2StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(R2StorageServiceImpl.class);
    private static final String DEFAULT_FOLDER = "uniclub/variants";
    private static final int MAX_IMAGE_DIMENSION = 1200;
    private static final float COMPRESSION_QUALITY = 0.82f;
    private static final long MAX_FILE_SIZE = 7L * 1024 * 1024; // 7MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final S3Client s3Client;

    @Value("${r2.bucket-name:uniclub-images}")
    private String bucketName;

    @Value("${r2.public-url:https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev}")
    private String publicUrl;

    public R2StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return uploadImage(file, DEFAULT_FOLDER);
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        try {
            // Nén ảnh & tối ưu dung lượng (Thumbnailator)
            OptimizationResult optimized = optimizeImage(file, extension);

            // Sinh tên file ngẫu nhiên UUID tránh trùng lặp
            String filename = UUID.randomUUID() + "." + optimized.format();
            String cleanFolder = (folder == null) ? "" : folder.trim().replaceAll("^/+|/+$", "");
            String objectKey = cleanFolder.isEmpty() ? filename : cleanFolder + "/" + filename;

            log.info("Uploading image to Cloudflare R2: bucket={}, key={}, originalSize={}B, optimizedSize={}B",
                    bucketName, objectKey, file.getSize(), optimized.bytes().length);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(optimized.contentType())
                    .contentLength((long) optimized.bytes().length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(optimized.bytes()));

            String cleanPublicUrl = (publicUrl == null) ? "" : publicUrl.trim().replaceAll("/+$", "");
            String finalUrl = cleanPublicUrl + "/" + objectKey;
            log.info("Image upload complete: {}", finalUrl);
            return finalUrl;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload image to R2: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi lưu trữ hình ảnh lên Cloudflare R2: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn hình ảnh cần xóa không được để trống");
        }

        try {
            String objectKey = extractObjectKey(fileUrl);
            if (objectKey != null && !objectKey.isEmpty()) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

                s3Client.deleteObject(deleteRequest);
                log.info("Deleted image from R2: bucket={}, key={}", bucketName, objectKey);
            }
        } catch (Exception e) {
            log.error("Failed to delete image from R2: url={}, error={}", fileUrl, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa hình ảnh trên Cloudflare R2: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File tải lên không được để trống");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Định dạng file không hợp lệ. Chỉ chấp nhận file hình ảnh");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dung lượng file vượt quá giới hạn tối đa cho phép (7MB)");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: " + ALLOWED_EXTENSIONS);
        }
    }

    private OptimizationResult optimizeImage(MultipartFile file, String extension) throws IOException {
        String ext = extension.toLowerCase();

        if ("png".equals(ext)) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                Thumbnails.of(file.getInputStream())
                        .size(MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION)
                        .outputFormat("png")
                        .toOutputStream(os);
                return new OptimizationResult(os.toByteArray(), "image/png", "png");
            } catch (Exception e) {
                log.error("Failed to process PNG image: {}", e.getMessage());
                throw new IllegalArgumentException("File ảnh PNG không hợp lệ hoặc dữ liệu bị hỏng");
            }
        } else if ("gif".equals(ext)) {
            byte[] bytes = file.getBytes();
            if (bytes.length < 6 || !(new String(bytes, 0, 4).equals("GIF8"))) {
                throw new IllegalArgumentException("File GIF không đúng định dạng chuẩn");
            }
            return new OptimizationResult(bytes, "image/gif", "gif");
        } else {
            // Cho JPG, JPEG, WEBP: nén kích thước tối đa 1200px, chất lượng 82%
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                Thumbnails.of(file.getInputStream())
                        .size(MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION)
                        .outputFormat("jpg")
                        .outputQuality(COMPRESSION_QUALITY)
                        .toOutputStream(os);
                return new OptimizationResult(os.toByteArray(), "image/jpeg", "jpg");
            } catch (Exception e) {
                log.error("Failed to process image: {}", e.getMessage());
                throw new IllegalArgumentException("File hình ảnh không hợp lệ hoặc dữ liệu bị hỏng");
            }
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "jpg";
    }

    private String extractObjectKey(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return null;
        }

        String cleanUrl = fileUrl.trim();
        String cleanPublicUrl = (publicUrl == null) ? "" : publicUrl.trim().replaceAll("/+$", "");

        if (!cleanPublicUrl.isEmpty() && cleanUrl.startsWith(cleanPublicUrl)) {
            String sub = cleanUrl.substring(cleanPublicUrl.length());
            return sub.replaceAll("^/+", "");
        }

        try {
            URI uri = URI.create(cleanUrl);
            String path = uri.getPath();
            if (path != null) {
                return path.replaceAll("^/+", "");
            }
        } catch (Exception ignored) {
        }

        return cleanUrl.replaceAll("^/+", "");
    }

    private record OptimizationResult(byte[] bytes, String contentType, String format) {}
}
