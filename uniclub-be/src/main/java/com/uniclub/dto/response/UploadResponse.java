package com.uniclub.dto.response;

public record UploadResponse(String url, String message) {
    public static UploadResponse of(String url, String message) {
        return new UploadResponse(url, message);
    }
}

