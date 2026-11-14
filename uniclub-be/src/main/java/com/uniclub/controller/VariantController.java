package com.uniclub.controller;

import com.uniclub.dto.request.Variant.CreateVariantRequest;
import com.uniclub.dto.request.Variant.UpdateVariantRequest;
import com.uniclub.dto.response.Variant.VariantImageUploadResponse;
import com.uniclub.dto.response.Variant.VariantResponse;
import com.uniclub.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/variants")
@Validated
public class VariantController {

    private final VariantService variantService;

    @Autowired
    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<VariantResponse> create(@Valid @RequestBody CreateVariantRequest request) {
        VariantResponse created = variantService.createVariant(request);
        return ResponseEntity
                .created(URI.create("/api/variants/" + created.getSku()))
                .body(created);
    }

    // UPDATE
    @PutMapping("/{sku}")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<VariantResponse> update(@PathVariable Integer sku,
                                                @Valid @RequestBody UpdateVariantRequest request) {
        VariantResponse updated = variantService.updateVariant(sku, request);
        return ResponseEntity.ok(updated);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<VariantResponse>> getAll(@RequestParam(required = false) Byte status) {
        return ResponseEntity.ok(variantService.getAllVariants(status));
    }

    // GET BY SKU
    @GetMapping("/{sku}")
    public ResponseEntity<VariantResponse> getBySku(@PathVariable Integer sku) {
        return ResponseEntity.ok(variantService.getBySku(sku));
    }

    // INCREASE STOCK
    @PutMapping("/{sku}/stock/increase")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<VariantResponse> increaseStock(@PathVariable Integer sku,
                                                      @RequestParam Integer amount) {
        VariantResponse updated = variantService.increaseStock(sku, amount);
        return ResponseEntity.ok(updated);
    }

    // DECREASE STOCK
    @PutMapping("/{sku}/stock/decrease")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<VariantResponse> decreaseStock(@PathVariable Integer sku,
                                                      @RequestParam Integer amount) {
        VariantResponse updated = variantService.decreaseStock(sku, amount);
        return ResponseEntity.ok(updated);
    }

    // CHECK COMBINATION EXISTS
    @GetMapping("/check-combination")
    public ResponseEntity<Boolean> checkCombination(@RequestParam Integer productId,
                                                  @RequestParam Integer sizeId,
                                                  @RequestParam Integer colorId) {
        boolean exists = variantService.existsByCombination(productId, sizeId, colorId);
        return ResponseEntity.ok(exists);
    }

    // UPLOAD IMAGE FOR VARIANT
    @PostMapping("/{sku}/upload-image")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<VariantImageUploadResponse> uploadVariantImage(@PathVariable Integer sku, 
                                                                         @RequestParam("file") MultipartFile file) {
        VariantResponse updated = variantService.uploadVariantImage(sku, file);
        return ResponseEntity.ok(VariantImageUploadResponse.of(
                updated, 
                updated.getImages(), 
                "Image uploaded and variant updated successfully"
        ));
    }

    // DELETE
    @DeleteMapping("/{sku}")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<Void> delete(@PathVariable Integer sku) {
        variantService.deleteVariantBySku(sku);
        return ResponseEntity.noContent().build();
    }
}
