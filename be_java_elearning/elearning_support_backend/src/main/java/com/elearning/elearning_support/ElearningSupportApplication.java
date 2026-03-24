package com.elearning.elearning_support;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.elearning.elearning_support.constants.SystemConstants;
import com.elearning.elearning_support.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;

@EnableAsync
@SpringBootApplication
@EnableScheduling
@Slf4j
public class ElearningSupportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElearningSupportApplication.class, args);
    }
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() {
        log.info("========= INITIALIZED TIMEZONE {} =========", Calendar.getInstance().getTimeZone().getID());

        // init shared app folder
        // data folder
        String sharedAppDirPath = FileUtils.getSharedAppDirectoryPath();
        File sharedDataFolder = new File(sharedAppDirPath + "/data");
        if (!sharedDataFolder.exists()) {
            sharedDataFolder.mkdirs();
        }
        // source folder
        File sharedSourceFolder = new File(sharedAppDirPath + "/source");
        if (!sharedSourceFolder.exists()) {
            sharedSourceFolder.mkdirs();
        }
        // logs folder
        File sharedLogsFolder = new File(SystemConstants.BASE_PATH + "/logs");
        if (!sharedLogsFolder.exists()) {
            sharedLogsFolder.mkdirs();
        }
        System.out.println(Arrays.toString(env.getActiveProfiles()));
        System.out.println(env.getProperty("spring.ai.openai.base-url"));
        log.info("========= INITIALIZED SHARED DATA, SOURCE AND LOGS FOLDER =========");
    }

}
