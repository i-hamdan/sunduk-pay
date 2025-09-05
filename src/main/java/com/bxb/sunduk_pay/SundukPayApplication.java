package com.bxb.sunduk_pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SundukPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SundukPayApplication.class, args);
	}

}
