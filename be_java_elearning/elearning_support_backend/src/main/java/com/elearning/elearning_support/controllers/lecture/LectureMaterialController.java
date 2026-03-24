package com.elearning.elearning_support.controllers.lecture;

import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.services.lecture.LectureMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/online/course")
public class LectureMaterialController {
    @Autowired
    LectureMaterialService lectureMaterialService;

    @PostMapping("/upload-fileAttachment")
    public ResponseEntity<List<LectureMaterialDto>> uploadFile(
            @RequestParam("file") MultipartFile[] multipartFiles)
            throws GeneralSecurityException, IOException {

        List<LectureMaterialDto> lectureMaterialDtoList = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            lectureMaterialDtoList.add(lectureMaterialService.uploadFile(file));
        }

        return ResponseEntity.ok(lectureMaterialDtoList);
    }
    @PostMapping("/upload-video")
    public ResponseEntity<LectureMaterialDto>uploadVideo(
            @RequestParam("file")MultipartFile file
            ){
       LectureMaterialDto lectureMaterialDto= lectureMaterialService.uploadVideo(file);
       return ResponseEntity.ok(lectureMaterialDto);
    }
    @DeleteMapping("/delete-video")
    public ResponseEntity<String>deleteVideo(@RequestBody LectureMaterialDto lectureMaterialDto){
        lectureMaterialService.deleteVideo(lectureMaterialDto);
        return  ResponseEntity.ok("delete video successfully");
    }
}