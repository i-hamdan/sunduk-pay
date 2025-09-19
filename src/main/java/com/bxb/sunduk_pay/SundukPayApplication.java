/**
 * The main package for SundukPay application.
 * Contains the Spring Boot entry point and other core components.
 */
package com.bxb.sunduk_pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the SundukPay Spring Boot application.
 */
@SpringBootApplication
@EnableScheduling
public class SundukPayApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(SundukPayApplication.class, args);
	}
}
