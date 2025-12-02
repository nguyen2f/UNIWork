package com.uniwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UniWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniWorkApplication.class, args);
	}

}
