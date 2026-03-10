package com.product.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmsBackendApplication.class, args);
	}

}
