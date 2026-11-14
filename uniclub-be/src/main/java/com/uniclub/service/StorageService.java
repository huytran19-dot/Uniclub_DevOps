package com.uniclub.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Upload and optimize image in the default storage folder.
     *
     * @param file the multipart file
     * @return public CDN URL
     */
    String uploadImage(MultipartFile file);

    /**
     * Upload and optimize image in a custom storage folder.
     *
     * @param file the multipart file
     * @param folder the target folder prefix
     * @return public CDN URL
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Delete an image from storage by its public URL or object key.
     *
     * @param fileUrl the public URL or key of the image
     */
    void deleteImage(String fileUrl);
}
