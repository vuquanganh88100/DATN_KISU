package com.elearning.elearning_support.services.lecture;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface LectureMaterialService {
    LectureMaterialDto uploadFile(MultipartFile multipartFile) throws GeneralSecurityException, IOException;
    LectureMaterialDto uploadVideo(MultipartFile multipartFile);
    void deleteVideo(LectureMaterialDto lectureMaterialDto);

}
