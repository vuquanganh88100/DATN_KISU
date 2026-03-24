package com.elearning.elearning_support.services.fileAttach.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.elearning.elearning_support.constants.CharacterConstants;
import com.elearning.elearning_support.constants.SystemConstants;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst.Resources;
import com.elearning.elearning_support.dtos.fileAttach.FileUploadResDTO;
import com.elearning.elearning_support.entities.fileAttach.FileAttach;
import com.elearning.elearning_support.enums.fileAttach.FileStoredTypeEnum;
import com.elearning.elearning_support.enums.fileAttach.FileTypeEnum;
import com.elearning.elearning_support.exceptions.exceptionFactory.ExceptionFactory;
import com.elearning.elearning_support.repositories.fileAttach.FileAttachRepository;
import com.elearning.elearning_support.services.fileAttach.FileAttachService;
import com.elearning.elearning_support.utils.DateUtils;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import com.elearning.elearning_support.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileAttachServiceImpl implements FileAttachService {

    private final Cloudinary cloudinary;

    private final FileAttachRepository fileAttachRepository;

    private final ExceptionFactory exceptionFactory;


    public FileAttachServiceImpl(Cloudinary cloudinary, FileAttachRepository fileAttachRepository, ExceptionFactory exceptionFactory) {
        this.cloudinary = cloudinary;
        this.fileAttachRepository = fileAttachRepository;
        this.exceptionFactory = exceptionFactory;
    }


    @Override
    public FileUploadResDTO uploadMPImageToCloudinary(MultipartFile multipartFile, FileTypeEnum fileType) {
        try {
            Map<String, Object> mapOptions = new HashMap<>();
            mapOptions.put("use_filename", true);
            mapOptions.put("resource_type", "image");
            Map uploadResponse = cloudinary.uploader().upload(multipartFile.getBytes(), mapOptions);
            String location = (String) uploadResponse.get("secure_url");

            if (Objects.isNull(location)) {
                throw exceptionFactory.fileUploadException(MessageConst.FileAttach.UPLOAD_ERROR_CODE, Resources.FILE_ATTACHED, MessageConst.UPLOAD_FAILED);
            }

            FileAttach fileAttach = FileAttach.builder()
                    .extension(FileUtils.getFileExt(multipartFile.getOriginalFilename()))
                    .type(fileType.getType())
                    .name(multipartFile.getOriginalFilename())
                    .externalLink(location)
                    .storedType(FileStoredTypeEnum.EXTERNAL_SERVER.getType())
                    .size(multipartFile.getSize())
                    .createdAt(new Date())
                    .createdBy(AuthUtils.getCurrentUserId())
                    .build();

            fileAttach = fileAttachRepository.save(fileAttach);

            return new FileUploadResDTO(fileAttach.getId(), fileAttach.getName(), fileAttach.getExternalLink(), fileAttach.getType(), fileAttach);

        } catch (IOException ex) {
            log.error(MessageConst.EXCEPTION_LOG_FORMAT, ex.getMessage(), ex.getCause().toString());
            return null;
        }
    }


    @Override
    public FileUploadResDTO uploadFileToCloudinary(File file, FileTypeEnum fileType) {
        try {
            if (Objects.isNull(file)) {
                return new FileUploadResDTO();
            }
            // Map option upload file cloudinary
            Map uploadResponse = uploadFileToCloudinary(file);
            String location = (String) uploadResponse.get("secure_url");
            if (Objects.isNull(location)) {
                throw exceptionFactory.fileUploadException(MessageConst.FileAttach.UPLOAD_ERROR_CODE, Resources.FILE_ATTACHED,
                    MessageConst.UPLOAD_FAILED);
            }

            // Create file attach to store in DB
            FileAttach fileAttach = FileAttach.builder()
                .extension(FileUtils.getFileExt(file))
                .type(fileType.getType())
                .name(file.getName())
                .externalLink(location)
                .storedType(FileStoredTypeEnum.EXTERNAL_SERVER.getType())
                .size(file.getTotalSpace())
                .createdAt(new Date())
                .createdBy(AuthUtils.getCurrentUserId())
                .build();
//            fileAttach = fileAttachRepository.save(fileAttach);
            // Delete file after upload cloudinary (temp not delete file after uploading)
//            boolean isDeleted = file.delete();
            return new FileUploadResDTO(fileAttach.getId(), fileAttach.getName(), fileAttach.getExternalLink(), fileAttach.getType(),
                fileAttach);
        } catch (IOException ex) {
            log.error(MessageConst.EXCEPTION_LOG_FORMAT, ex.getMessage(), ex.getCause().toString());
            return null;
        }
    }

    @Override
    public Map<String, FileUploadResDTO> uploadListFileToCloudinary(Map<String, File> mapFiles, FileTypeEnum fileType) {
//        Long currentUserId = AuthUtils.getCurrentUserId();
        Map<String, FileUploadResDTO> mapUploadResults = new HashMap<>();
        for (Map.Entry<String, File> mapItem : mapFiles.entrySet()) {
            File file = mapItem.getValue();
            try {
                if (Objects.isNull(file)) {
                    continue;
                }
                Map uploadResponse = uploadFileToCloudinary(file);
                String location = (String) uploadResponse.get("secure_url");
                if (Objects.isNull(location)) {
                    throw exceptionFactory.fileUploadException(MessageConst.FileAttach.UPLOAD_ERROR_CODE, Resources.FILE_ATTACHED,
                        MessageConst.UPLOAD_FAILED);
                }

                // Create file attach to store in DB
                FileAttach fileAttach = FileAttach.builder()
                    .extension(FileUtils.getFileExt(file))
                    .type(fileType.getType())
                    .name(file.getName())
                    .externalLink(location)
                    .storedType(FileStoredTypeEnum.EXTERNAL_SERVER.getType())
                    .size(file.getTotalSpace())
                    .createdAt(new Date())
                    .build();
//                boolean isDeleted = file.delete();
                mapUploadResults.put(mapItem.getKey(),
                    new FileUploadResDTO(null, file.getName(), location, fileType.getType(), FileStoredTypeEnum.EXTERNAL_SERVER,
                        fileAttach));
            } catch (IOException ex) {
                log.error(MessageConst.EXCEPTION_LOG_FORMAT, ex.getMessage(), ex.getCause().toString());
            }
        }
        return mapUploadResults;
    }

    @Override
    public Map<String, FileUploadResDTO> uploadListFileToInternalServer(Map<String, File> mapFiles, FileTypeEnum fileType) {
        Map<String, FileUploadResDTO> mapUploadResults = new HashMap<>();
        final String dateFolder = DateUtils.formatDateWithPattern(DateUtils.getCurrentDateTime(), DateUtils.FORMAT_DATE_DD_MM_YYYY);
        final String baseUploadLocation = (Objects.equals(fileType, FileTypeEnum.DOCUMENT)
            ? SystemConstants.DOCUMENT_UPLOAD_LOCATION : SystemConstants.IMAGE_UPLOAD_LOCATION) + CharacterConstants.SLASH + dateFolder;
        final String baseUploadUrl = (Objects.equals(fileType, FileTypeEnum.DOCUMENT)
            ? SystemConstants.DOCUMENT_UPLOAD_URL_PATH : SystemConstants.IMAGE_UPLOAD_URL_PATH) + CharacterConstants.SLASH + dateFolder;

        // check current date folder if exists
        FileUtils.createDirectory(baseUploadLocation);
        for (Map.Entry<String, File> mapItem : mapFiles.entrySet()) {
            File file = mapItem.getValue();
            String newFileName = String.format("/%s_%s", System.currentTimeMillis(), file.getName());
            String newFileUploadUrl = baseUploadUrl + newFileName;
            try {
                FileInputStream fis = new FileInputStream(file);
                File newFile = FileUtils.copyInputStreamToFile(baseUploadLocation + newFileName, fis);
                FileAttach fileAttach = FileAttach.builder()
                    .extension(FileUtils.getFileExt(file))
                    .type(fileType.getType())
                    .name(newFileName)
                    .storedType(FileStoredTypeEnum.INTERNAL_SERVER.getType())
                    .size(file.getTotalSpace())
                    .createdAt(new Date())
                    .build();
                mapUploadResults.put(mapItem.getKey(),
                    new FileUploadResDTO(null, newFileName, newFileUploadUrl, fileType.getType(), FileStoredTypeEnum.INTERNAL_SERVER,
                        fileAttach));
            } catch (Exception ex) {
                log.error(MessageConst.EXCEPTION_LOG_FORMAT, ex.getMessage(), ex.getCause());
            }
        }
        return mapUploadResults;
    }

    @Override
    public FileUploadResDTO uploadDocument(MultipartFile multipartFile, FileTypeEnum fileType) {
        // generate file name
        File uploadFile = FileUtils.covertMultipartToFile(SystemConstants.RESOURCE_PATH + FileUtils.DOCUMENTS_STORED_LOCATION, multipartFile);
        if (Objects.isNull(uploadFile)) {
            throw exceptionFactory.fileUploadException(MessageConst.FileAttach.UPLOAD_ERROR_CODE, Resources.FILE_ATTACHED,
                MessageConst.UPLOAD_FAILED);
        }
        String location = FileUtils.DOCUMENTS_RESOURCE_DIR + uploadFile.getName();

        // create file attach
        FileAttach fileAttach = FileAttach.builder()
            .extension(FileUtils.getFileExt(uploadFile))
            .type(fileType.getType())
            .name(uploadFile.getName())
            .filePath(location)
            .size(multipartFile.getSize())
            .storedType(FileStoredTypeEnum.INTERNAL_SERVER.getType())
            .createdAt(new Date())
            .createdBy(AuthUtils.getCurrentUserId())
            .build();
        fileAttach = fileAttachRepository.save(fileAttach);
        return new FileUploadResDTO(fileAttach.getId(), fileAttach.getName(), fileAttach.getFilePath(), fileAttach.getType(), fileAttach);
    }

    @Override
    public String uploadImageCourse(MultipartFile file) {
        try {
            Map<String, String> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            return uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
    @Override
     public String getPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        String publicId = publicIdWithExtension.split("\\.")[0];
        return publicId;
    }
    @Override
     public void deleteImageFromCloudinary(String publicId) {
        try {
            Map<String, String> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if ("ok".equals(deleteResult.get("result"))) {
                System.out.println("Successfully deleted image from Cloudinary.");
            } else {
                System.out.println("Failed to delete image from Cloudinary.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error delete image from Cloudinary", e);
        }
    }

    @Override
    public Map<String,String> uploadVideoToCloudinary(MultipartFile multipartFile) {
        Map<String,String>uploadResult=new HashMap<>();
        try {
            uploadResult = cloudinary.uploader().uploadLarge(multipartFile.getBytes(), ObjectUtils.asMap("resource_type", "video"));
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
        return  uploadResult;
    }
    @Override
    public void deleteVideoFromCloudinary(String publicId) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "video");  // Xóa video
            Map<String, String> deleteResult = cloudinary.uploader().destroy(publicId, options);
            if ("ok".equals(deleteResult.get("result"))) {
                System.out.println("Successfully deleted video from Cloudinary.");
            } else {
                System.out.println("Failed to delete video from Cloudinary.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting video from Cloudinary", e);
        }
    }


    /**
     * Upload a file to Cloudinary
     */
    private Map uploadFileToCloudinary(File file) throws IOException {
        // Map option upload file cloudinary
        Map<String, Object> mapOptions = new HashMap<>();
        mapOptions.put("use_filename", true);
        mapOptions.put("resource_type", "auto");
        return cloudinary.uploader().upload(file, mapOptions);
    }
}
