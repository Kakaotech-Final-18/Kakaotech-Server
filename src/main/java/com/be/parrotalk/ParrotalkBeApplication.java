package com.be.parrotalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ParrotalkBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParrotalkBeApplication.class, args);
	}

}
