package com.uniclub.dto.response.Variant;

public record VariantImageUploadResponse(VariantResponse variant, String imageUrl, String message) {
    public static VariantImageUploadResponse of(VariantResponse variant, String imageUrl, String message) {
        return new VariantImageUploadResponse(variant, imageUrl, message);
    }
}

