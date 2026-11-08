package com.uniclub.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uniclub.dto.request.Variant.CreateVariantRequest;
import com.uniclub.dto.request.Variant.UpdateVariantRequest;
import com.uniclub.dto.response.Variant.VariantResponse;
import com.uniclub.entity.Color;
import com.uniclub.entity.Product;
import com.uniclub.entity.Size;
import com.uniclub.entity.Variant;
import com.uniclub.exception.ResourceNotFoundException;
import com.uniclub.repository.ColorRepository;
import com.uniclub.repository.ProductRepository;
import com.uniclub.repository.SizeRepository;
import com.uniclub.repository.VariantRepository;
import com.uniclub.service.StorageService;
import com.uniclub.service.VariantService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VariantServiceImpl implements VariantService {

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private StorageService storageService;

    // CREATE
    @Override
    public VariantResponse createVariant(CreateVariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "id", request.getProductId()));

        Size size = null;
        if (request.getSizeId() != null) {
            size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Size", "id", request.getSizeId()));
        }

        Color color = null;
        if (request.getColorId() != null) {
            color = colorRepository.findById(request.getColorId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Color", "id", request.getColorId()));
        }

        Variant variant = new Variant();
        variant.setProduct(product);
        variant.setSize(size);
        variant.setColor(color);
        variant.setImages(request.getImages());
        variant.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        variant.setPrice(request.getPrice());

        Variant savedVariant = variantRepository.save(variant);
        return VariantResponse.fromEntity(savedVariant);
    }

    // GET by SKU
    @Override
    public VariantResponse getBySku(Integer skuId) {
        Variant v = variantRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", skuId));
        return VariantResponse.fromEntity(v);
    }

    // GET all (optional filter by status)
    @Override
    public List<VariantResponse> getAllVariants(Byte status) {
        List<Variant> list = (status == null)
                ? variantRepository.findAll()
                : variantRepository.findByStatus(status);
        return list.stream().map(VariantResponse::fromEntity).toList();
    }

    // UPDATE
    @Override
    public VariantResponse updateVariant(Integer skuId, UpdateVariantRequest request) {
        Variant v = variantRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", skuId));

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product", "id", request.getProductId()));
            v.setProduct(product);
        }

        if (request.getSizeId() != null) {
            Size size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Size", "id", request.getSizeId()));
            v.setSize(size);
        }

        if (request.getColorId() != null) {
            Color color = colorRepository.findById(request.getColorId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Color", "id", request.getColorId()));
            v.setColor(color);
        }

        if (request.getImages() != null) v.setImages(request.getImages());
        if (request.getQuantity() != null) v.setQuantity(request.getQuantity());
        if (request.getPrice() != null) v.setPrice(request.getPrice());
        if (request.getStatus() != null) v.setStatus(request.getStatus());

        Variant updatedVariant = variantRepository.save(v);
        return VariantResponse.fromEntity(updatedVariant);
    }

    // DELETE
    @Override
    public void deleteVariantBySku(Integer skuId) {
        Variant v = variantRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", skuId));
        if (v.getImages() != null && !v.getImages().isBlank()) {
            storageService.deleteImage(v.getImages());
        }
        variantRepository.delete(v);
    }

    // STOCK +
    @Override
    public VariantResponse increaseStock(Integer skuId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Số lượng tăng phải > 0");
        }
        Variant v = variantRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", skuId));
        v.setQuantity((v.getQuantity() == null ? 0 : v.getQuantity()) + amount);
        return VariantResponse.fromEntity(variantRepository.save(v));
    }

    // STOCK -
    @Override
    public VariantResponse decreaseStock(Integer skuId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Số lượng giảm phải > 0");
        }
        Variant v = variantRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", skuId));

        int current = (v.getQuantity() == null ? 0 : v.getQuantity());
        int next = current - amount;
        if (next < 0) {
            throw new RuntimeException("Số lượng không đủ. Hiện có: " + current);
        }
        v.setQuantity(next);
        return VariantResponse.fromEntity(variantRepository.save(v));
    }

    @Override
    public boolean existsByCombination(Integer productId, Integer sizeId, Integer colorId) {
        return false;
    }

    @Override
    public VariantResponse updateVariantImage(Integer sku, String imageUrl) {
        Variant variant = variantRepository.findById(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "sku", sku));
        
        variant.setImages(imageUrl);
        Variant updatedVariant = variantRepository.save(variant);
        
        return VariantResponse.fromEntity(updatedVariant);
    }

    @Override
    public VariantResponse uploadVariantImage(Integer sku, MultipartFile file) {
        Variant variant = variantRepository.findById(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "sku", sku));

        // Xóa ảnh cũ trên storage nếu đã tồn tại
        String oldImageUrl = variant.getImages();
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            storageService.deleteImage(oldImageUrl);
        }

        String newImageUrl = storageService.uploadImage(file, "uniclub/variants");
        variant.setImages(newImageUrl);
        Variant updatedVariant = variantRepository.save(variant);
        return VariantResponse.fromEntity(updatedVariant);
    }

}
