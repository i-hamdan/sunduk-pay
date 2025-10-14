package com.bxb.sunduk_pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the SundukPay Spring Boot application.
 */
@EnableCaching
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
