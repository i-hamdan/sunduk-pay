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

/**Non-static application name to avoid checkstyle error.*/
private final String appName = "Sunduk Pay Application";
/**
* Main method to start the Spring Boot application.
* @param args command-line arguments passed to the application
*/
public static void main(final String[] args) {
SpringApplication.run(SundukPayApplication.class, args);
}
}
