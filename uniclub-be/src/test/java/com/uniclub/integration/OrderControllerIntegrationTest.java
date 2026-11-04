package com.uniclub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniclub.dto.request.Order.CreateOrderRequest;
import com.uniclub.dto.request.Order.CreateOrderVariantRequest;
import com.uniclub.entity.*;
import com.uniclub.entity.enums.OrderStatus;
import com.uniclub.entity.enums.PaymentMethod;
import com.uniclub.entity.enums.PaymentStatus;
import com.uniclub.repository.*;
import com.uniclub.service.VNPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("uniclub_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderVariantRepository orderVariantRepository;

    @MockBean
    private VNPayService vnPayService;

    private User user;
    private Variant variant;
    private Variant anotherVariant;

    @BeforeEach
    void setUp() {
        orderVariantRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        variantRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setEmail("buyer@example.com");
        user.setPassword("secret");
        user.setFullname("Buyer");
        user.setPhone("0900000000");
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Product product = new Product();
        product.setName("Áo thun");
        productRepository.save(product);

        variant = createVariant(product, 300_000);
        anotherVariant = createVariant(product, 250_000);
    }

    @Test
    void createOrder_codFlowShouldApplyFreeShipping() throws Exception {
        CreateOrderRequest request = baseRequest(List.of(
                variantRequest(variant.getSku(), 1, 300_000),
                variantRequest(anotherVariant.getSku(), 1, 250_000)
        ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shippingFee").value(0))
                .andExpect(jsonPath("$.total").value(550_000));

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(paymentRepository.count()).isEqualTo(1);
    }

    @Test
    void createOrder_shouldRejectMissingPhone() throws Exception {
        CreateOrderRequest request = baseRequest(List.of(
                variantRequest(variant.getSku(), 1, 200_000)
        ));
        request.setRecipientPhone("  ");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retryVnPayPayment_shouldReturnNewPaymentUrl() throws Exception {
        Order order = new Order();
        order.setUser(user);
        order.setRecipientName("Buyer");
        order.setRecipientPhone("0900000000");
        order.setShippingAddress("123 Đường ABC");
        order.setStatus(OrderStatus.PENDING);
        order.setShippingFee(0);
        order.setTotal(120_000);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.VNPay);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotal());
        paymentRepository.save(payment);

        when(vnPayService.createPaymentUrl(any(), any(), any(), any())).thenReturn("https://pay.mock");

        mockMvc.perform(post("/api/orders/{id}/retry-payment", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("https://pay.mock"));

        verify(vnPayService).createPaymentUrl(order.getId(),
                "Thanh toan lai don hang " + order.getId(),
                "127.0.0.1",
                "vn");
    }

    private CreateOrderRequest baseRequest(List<CreateOrderVariantRequest> variants) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(user.getId());
        request.setOrderVariants(variants);
        request.setRecipientName("Buyer");
        request.setRecipientPhone("0900000000");
        request.setShippingAddress("123 Đường ABC");
        request.setPaymentMethod(PaymentMethod.COD);
        return request;
    }

    private CreateOrderVariantRequest variantRequest(Integer sku, int quantity, int price) {
        CreateOrderVariantRequest req = new CreateOrderVariantRequest();
        req.setVariantSku(sku);
        req.setQuantity(quantity);
        req.setPrice(price);
        return req;
    }

    private Variant createVariant(Product product, int price) {
        Variant v = new Variant();
        v.setProduct(product);
        v.setPrice(price);
        v.setQuantity(100);
        return variantRepository.save(v);
    }
}

