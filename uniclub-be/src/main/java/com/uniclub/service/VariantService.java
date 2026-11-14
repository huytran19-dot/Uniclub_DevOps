package com.uniclub.service;

import com.uniclub.dto.request.Variant.CreateVariantRequest;
import com.uniclub.dto.request.Variant.UpdateVariantRequest;
import com.uniclub.dto.response.Variant.VariantResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VariantService {

    VariantResponse createVariant(CreateVariantRequest request);
    VariantResponse getBySku(Integer sku);

    List<VariantResponse> getAllVariants(Byte status);

    VariantResponse updateVariant(Integer sku, UpdateVariantRequest request);

    void deleteVariantBySku(Integer sku);

    VariantResponse increaseStock(Integer sku, Integer amount);
    VariantResponse decreaseStock(Integer sku, Integer amount);

    boolean existsByCombination(Integer productId, Integer sizeId, Integer colorId);
    
    VariantResponse updateVariantImage(Integer sku, String imageUrl);

    VariantResponse uploadVariantImage(Integer sku, MultipartFile file);
}
