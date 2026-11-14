package com.uniclub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    
    @Value("${vnpay.tmn-code:}")
    private String tmnCode;
    
    @Value("${vnpay.hash-secret:}")
    private String hashSecret;
    
    @Value("${vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;
    
    @Value("${vnpay.return-url:http://localhost:5174/payment/vnpay-return}")
    private String returnUrl;
    
    @Value("${vnpay.ipn-url:http://localhost:8080/api/vnpay/ipn}")
    private String ipnUrl;
    
    @Value("${vnpay.version:2.1.0}")
    private String version;
    
    @Value("${vnpay.command:pay}")
    private String command;
    
    @Value("${vnpay.order-type:other}")
    private String orderType;
    
    public String getTmnCode() {
        return tmnCode;
    }
    
    public String getHashSecret() {
        return hashSecret;
    }
    
    public String getVnpayUrl() {
        return vnpayUrl;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public String getIpnUrl() {
        return ipnUrl;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getOrderType() {
        return orderType;
    }
}
