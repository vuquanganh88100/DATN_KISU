package com.elearning.elearning_support.configurations.firebase;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FirebaseConfig {

//    private static final String FIREBASE_CONFIG_PATH = "D:/download/multiple_exam_2024_early_release-main/multiple_exam_2024_early_release-main/be_java_elearning/elearning_support_backend/elearningsupportsystem-55777-firebase-adminsdk-8oqd8-6bb7d37704.json";
//
//    private static final String APP_NAME = "myApp"; // Change to your desired app name
    @Value("${firebase.config}")
    private String firebaseConfigPath;

    @Value("${firebase.app-name}")
    private String appName;

    @Bean
    FirebaseMessaging firebaseMessaging() {
        try {
            System.out.println(firebaseConfigPath);
            log.info("Loading Firebase config from: {}", firebaseConfigPath);
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(firebaseConfigPath));
            FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions, appName);
            log.info("=== LOG : { Initialize FirebaseApp successfully! } ===");
            return FirebaseMessaging.getInstance(firebaseApp);
        } catch (Exception exception) {
            log.error("==== ERROR : { Initialize FirebaseApp fail } ===", exception);
            return null;
        }
    }
}
