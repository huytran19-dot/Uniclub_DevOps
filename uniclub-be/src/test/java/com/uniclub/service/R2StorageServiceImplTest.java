package com.uniclub.service;

import com.uniclub.service.impl.R2StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class R2StorageServiceImplTest {

    @Mock
    private S3Client s3Client;

    private R2StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new R2StorageServiceImpl(s3Client);
        ReflectionTestUtils.setField(storageService, "bucketName", "uniclub-images");
        ReflectionTestUtils.setField(storageService, "publicUrl", "https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev");
    }

    @Test
    void uploadImage_shouldThrowWhenFileIsNull() {
        assertThatThrownBy(() -> storageService.uploadImage(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File tải lên không được để trống");
    }

    @Test
    void uploadImage_shouldThrowWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertThatThrownBy(() -> storageService.uploadImage(emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File tải lên không được để trống");
    }

    @Test
    void uploadImage_shouldThrowWhenNotAnImage() {
        MockMultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        assertThatThrownBy(() -> storageService.uploadImage(textFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Định dạng file không hợp lệ");
    }

    @Test
    void uploadImage_shouldThrowWhenDisallowedExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "script.exe", "image/jpeg", "fake-img".getBytes());
        assertThatThrownBy(() -> storageService.uploadImage(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Định dạng file không được hỗ trợ");
    }

    @Test
    void uploadImage_shouldThrowWhenFileSizeExceedsLimit() {
        byte[] largeBytes = new byte[7 * 1024 * 1024 + 1];
        MockMultipartFile largeFile = new MockMultipartFile("file", "large.jpg", "image/jpeg", largeBytes);
        assertThatThrownBy(() -> storageService.uploadImage(largeFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dung lượng file vượt quá giới hạn tối đa cho phép (7MB)");
    }

    @Test
    void uploadImage_shouldOptimizeAndUploadValidJpeg() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile validImage = new MockMultipartFile(
                "file", "product.jpg", "image/jpeg", baos.toByteArray()
        );

        String resultUrl = storageService.uploadImage(validImage, "uniclub/products");

        assertThat(resultUrl).startsWith("https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/products/");
        assertThat(resultUrl).endsWith(".jpg");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deleteImage_shouldThrowWhenUrlIsBlank() {
        assertThatThrownBy(() -> storageService.deleteImage("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Đường dẫn hình ảnh cần xóa không được để trống");
    }

    @Test
    void deleteImage_shouldCallS3ClientWithExtractedKey() {
        String url = "https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/sample.jpg";

        storageService.deleteImage(url);

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}

