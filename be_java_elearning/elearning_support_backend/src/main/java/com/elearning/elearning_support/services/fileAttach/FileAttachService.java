package com.elearning.elearning_support.services.fileAttach;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.elearning.elearning_support.dtos.fileAttach.FileUploadResDTO;
import com.elearning.elearning_support.enums.fileAttach.FileTypeEnum;

@Service
public interface FileAttachService {

    /**
     * Upload image file
     */
    FileUploadResDTO uploadMPImageToCloudinary(MultipartFile multipartFile, FileTypeEnum fileType);

    /**
     * Upload file to cloudinary
     */
    FileUploadResDTO uploadFileToCloudinary(File file, FileTypeEnum fileType);

    /**
     * Upload list file to cloudinary
     */
    Map<String, FileUploadResDTO> uploadListFileToCloudinary(Map<String, File> mapFiles, FileTypeEnum fileType);

    Map<String, FileUploadResDTO> uploadListFileToInternalServer(Map<String, File> mapFiles, FileTypeEnum fileType);

    /**
     * Upload document file
     */
    FileUploadResDTO uploadDocument(MultipartFile multipartFile, FileTypeEnum fileType);


    // online course

    String uploadImageCourse(MultipartFile multipartFile);
    String getPublicIdFromUrl(String imageUrl);
    void deleteImageFromCloudinary(String publicId);
    Map<String,String> uploadVideoToCloudinary(MultipartFile multipartFile);
    void deleteVideoFromCloudinary(String publicId);
}
