package com.forgeurself.ob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class OpenBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenBankingApplication.class, args);
	}
}
