package com.elearning.elearning_support.configurations.googleDriveApi;

import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.utils.file.FileUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
@Configuration
public class GoogleDriveApi {
    private static final GsonFactory JSON_FACTORY= GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACOUNT_KEY_PATH = "D:\\download\\multiple_exam_2024_early_release\\be_java_elearning\\elearning_support_backend\\ggdrive-api-lms-online-442907-43065e8324aa.json";
    private static Drive createDriveService() throws GeneralSecurityException, IOException {

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .build();

    }
    public static String uploadFileAttachment(File file) throws GeneralSecurityException, IOException {
        String url=null;
        try{
            String folderId = "1LebKHTsvh7LPiDlHH6rHdMzWAUe46D7n";
            Drive drive = createDriveService();
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent(FileUtils.getMimeType(file),file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();
             url = "https://drive.google.com/uc?export=view&id="+uploadedFile.getId();
            System.out.println(" URL: " + url);

        }catch (Exception e){
            System.out.println(e.getMessage());

        }
        return url;
    }

}
