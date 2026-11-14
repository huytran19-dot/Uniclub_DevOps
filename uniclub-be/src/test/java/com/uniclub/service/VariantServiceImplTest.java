package com.uniclub.service;

import com.uniclub.dto.request.Variant.CreateVariantRequest;
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
import com.uniclub.service.impl.VariantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariantServiceImplTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SizeRepository sizeRepository;

    @Mock
    private ColorRepository colorRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private VariantServiceImpl variantService;

    private Product product;
    private Size size;
    private Color color;
    private Variant variant;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Áo Polo");

        size = new Size();
        size.setId(1);
        size.setName("L");

        color = new Color();
        color.setId(1);
        color.setName("Trắng");

        variant = new Variant();
        variant.setSku(100);
        variant.setProduct(product);
        variant.setSize(size);
        variant.setColor(color);
        variant.setQuantity(10);
        variant.setPrice(120_000);
    }

    @Test
    void getVariantsByColorId_shouldReturnVariantsWithSpecificColor() {
        Variant variant2 = new Variant();
        variant2.setSku(101);
        variant2.setProduct(product);
        variant2.setSize(size);
        variant2.setColor(color);
        variant2.setQuantity(5);

        Color color2 = new Color();
        color2.setId(2);
        color2.setName("Đen");
        Variant variant3 = new Variant();
        variant3.setSku(102);
        variant3.setProduct(product);
        variant3.setSize(size);
        variant3.setColor(color2);
        variant3.setQuantity(8);
    }

    @Test
    void createVariant_shouldCreateSuccessfully() {
        CreateVariantRequest request = new CreateVariantRequest();
        request.setProductId(1);
        request.setSizeId(1);
        request.setColorId(1);
        request.setQuantity(10);
        request.setPrice(120_000);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(sizeRepository.findById(1)).thenReturn(Optional.of(size));
        when(colorRepository.findById(1)).thenReturn(Optional.of(color));
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> {
            Variant v = invocation.getArgument(0);
            v.setSku(100);
            return v;
        });

        VariantResponse response = variantService.createVariant(request);

        assertThat(response.getSku()).isEqualTo(100);
        assertThat(response.getQuantity()).isEqualTo(10);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test
    void createVariant_shouldThrowWhenProductNotFound() {
        CreateVariantRequest request = new CreateVariantRequest();
        request.setProductId(999);

        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.createVariant(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(variantRepository, never()).save(any());
    }

    @Test
    void getBySku_shouldReturnVariant() {
        when(variantRepository.findById(100)).thenReturn(Optional.of(variant));

        VariantResponse response = variantService.getBySku(100);

        assertThat(response.getSku()).isEqualTo(100);
    }

    @Test
    void getBySku_shouldThrowWhenNotFound() {
        when(variantRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.getBySku(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllVariants_shouldReturnAllVariants() {
        Variant variant2 = new Variant();
        variant2.setSku(101);

        when(variantRepository.findAll()).thenReturn(Arrays.asList(variant, variant2));

        List<VariantResponse> responses = variantService.getAllVariants(null);

        assertThat(responses).hasSize(2);
    }

    @Test
    void getAllVariants_shouldFilterByStatus() {
        Variant variant2 = new Variant();
        variant2.setSku(101);
        variant2.setStatus((byte) 0);

        when(variantRepository.findByStatus((byte) 1)).thenReturn(List.of(variant));

        List<VariantResponse> responses = variantService.getAllVariants((byte) 1);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSku()).isEqualTo(100);
    }

    @Test
    void deleteVariantBySku_shouldDeleteOldImageWhenExists() {
        variant.setImages("https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/old.jpg");
        when(variantRepository.findById(100)).thenReturn(Optional.of(variant));

        variantService.deleteVariantBySku(100);

        verify(storageService).deleteImage("https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/old.jpg");
        verify(variantRepository).delete(variant);
    }

    @Test
    void uploadVariantImage_shouldUploadAndSaveNewImageUrl() {
        when(variantRepository.findById(100)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "shirt.jpg", "image/jpeg", "image-content".getBytes()
        );
        String expectedUrl = "https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/new-image.jpg";
        when(storageService.uploadImage(file, "uniclub/variants")).thenReturn(expectedUrl);

        VariantResponse response = variantService.uploadVariantImage(100, file);

        assertThat(response.getImages()).isEqualTo(expectedUrl);
        verify(storageService).uploadImage(file, "uniclub/variants");
        verify(variantRepository).save(variant);
    }

    @Test
    void uploadVariantImage_shouldDeleteOldImageIfAlreadyPresent() {
        variant.setImages("https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/previous.jpg");
        when(variantRepository.findById(100)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "new-shirt.png", "image/png", "png-content".getBytes()
        );
        String expectedUrl = "https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/latest.png";
        when(storageService.uploadImage(file, "uniclub/variants")).thenReturn(expectedUrl);

        VariantResponse response = variantService.uploadVariantImage(100, file);

        verify(storageService).deleteImage("https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/previous.jpg");
        verify(storageService).uploadImage(file, "uniclub/variants");
        assertThat(response.getImages()).isEqualTo(expectedUrl);
    }
}
