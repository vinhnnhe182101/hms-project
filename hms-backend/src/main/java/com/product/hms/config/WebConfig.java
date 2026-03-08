package com.product.hms.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:5173") // Cổng React/Vite
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                        .allowedHeaders("*")
//                        .allowCredentials(true)
//                        .maxAge(3600);
//            }
//        };
//    }
}
