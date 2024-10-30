package com.hotel.booking.configuration;

import com.hotel.booking.util.ClientSide;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        ClientSide.CLIENT_SITE_URL,
                        "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html",
                        "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html**",
                        "https://sandbox.vnpayment.vn/**",
                        "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"
                ) // Thay bằng URL của ứng dụng React
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true);

    }
}