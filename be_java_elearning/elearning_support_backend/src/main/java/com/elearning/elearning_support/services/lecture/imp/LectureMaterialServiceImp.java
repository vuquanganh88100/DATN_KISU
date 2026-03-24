package com.elearning.elearning_support.services.lecture.imp;

import com.elearning.elearning_support.configurations.googleDriveApi.GoogleDriveApi;
import com.elearning.elearning_support.constants.SystemConstants;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.entities.fileAttach.FileAttach;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.mapper.lectureMaterial.LectureMaterialMapper;
import com.elearning.elearning_support.repositories.lecture.LectureMaterialRepository;
import com.elearning.elearning_support.services.fileAttach.FileAttachService;
import com.elearning.elearning_support.services.lecture.LectureMaterialService;
import com.elearning.elearning_support.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Service
public class LectureMaterialServiceImp implements LectureMaterialService {
    @Autowired
    LectureMaterialRepository lectureMaterialRepository;
    @Autowired
    FileAttachService fileAttachService;
    @Override
    public LectureMaterialDto uploadFile(MultipartFile multipartFile) throws GeneralSecurityException, IOException {
        File file = FileUtils.covertMultipartToFile("D:\\download\\multiple_exam_2024_early_release\\be_java_elearning\\elearning_support_backend\\src\\main\\resources\\upload", multipartFile);
        try {
            LectureMaterialDto lectureMaterialDto = new LectureMaterialDto();
            lectureMaterialDto.setFileEx(FileUtils.getFileExt(file));
            lectureMaterialDto.setFileName(FileUtils.getFileBodyName(file).replaceFirst("upload",""));
            lectureMaterialDto.setExternalLink(GoogleDriveApi.uploadFileAttachment(file));
            lectureMaterialDto.setSize((int) multipartFile.getSize());
            lectureMaterialDto.setType(1);
            lectureMaterialDto.setStoredType(1);
            LectureMaterial lectureMaterial= LectureMaterialMapper.lectureMaterial(lectureMaterialDto);
            LectureMaterial savedLectureMaterial = lectureMaterialRepository.save(lectureMaterial);
            lectureMaterialDto.setId(savedLectureMaterial.getId());
            return lectureMaterialDto;
        } finally {
            if(file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public LectureMaterialDto uploadVideo(MultipartFile file) {
        Map<String,String> cloudinary=fileAttachService.uploadVideoToCloudinary(file);
        LectureMaterialDto lectureMaterialDto=new LectureMaterialDto();
        lectureMaterialDto.setExternalLink(cloudinary.get("secure_url"));
        lectureMaterialDto.setFileEx("mp4");
        lectureMaterialDto.setSize((int) file.getSize());
        lectureMaterialDto.setType(1);
        lectureMaterialDto.setStoredType(0);
        LectureMaterial lectureMaterial= LectureMaterialMapper.lectureMaterial(lectureMaterialDto);

        LectureMaterial savedLectureMaterial=lectureMaterialRepository.save(lectureMaterial);
        lectureMaterialDto.setId(savedLectureMaterial.getId());
        return lectureMaterialDto;
    }

    @Override
    public void deleteVideo(LectureMaterialDto lectureMaterialDto) {
        String oldUrl=lectureMaterialDto.getExternalLink();
        String oldPublicId=fileAttachService.getPublicIdFromUrl(oldUrl);
        if (oldPublicId != null) {
            fileAttachService.deleteVideoFromCloudinary(oldPublicId);  // delete old video by publicId
            lectureMaterialRepository.deleteById(lectureMaterialDto.getId());
        }
    }


}
