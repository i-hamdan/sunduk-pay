package com.bxb.sunduk_pay.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for initializing Firebase.
 */
@Configuration
public class FirebaseConfig {

    /** Initializes Firebase with service account credentials. */
    @PostConstruct
    public void initialize() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream(
                "/firebase-service-account.json");

        if (serviceAccount == null) {
            throw new IOException(
                    "firebase-service-account.json not found in classpath!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");
        }
    }
}
