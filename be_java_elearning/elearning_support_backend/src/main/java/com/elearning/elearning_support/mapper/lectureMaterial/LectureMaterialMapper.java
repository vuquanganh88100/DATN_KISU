package com.elearning.elearning_support.mapper.lectureMaterial;

import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.mapstruct.Mapper;

public class LectureMaterialMapper {
    public static LectureMaterial lectureMaterial(LectureMaterialDto lectureMaterialDto){
        LectureMaterial lectureMaterial=new LectureMaterial();
        lectureMaterial.setSize(lectureMaterialDto.getSize());
        lectureMaterial.setExternalLink(lectureMaterialDto.getExternalLink());
        lectureMaterial.setCreated_by(lectureMaterialDto.getCreadtedBy());
        lectureMaterial.setStoredType(lectureMaterialDto.getStoredType());
        lectureMaterial.setFilePath(lectureMaterialDto.getFilePath());
        lectureMaterial.setType(lectureMaterialDto.getType());
        lectureMaterial.setCreated_by(AuthUtils.getCurrentUserId());
        lectureMaterial.setFileEx(lectureMaterialDto.getFileEx());
        lectureMaterial.setFileName(lectureMaterialDto.getFileName());
        return  lectureMaterial;
    }
    public static LectureMaterialDto toDto(LectureMaterial lectureMaterial){
        LectureMaterialDto lectureMaterialDto=new LectureMaterialDto();
        lectureMaterialDto.setId(lectureMaterial.getId());
        lectureMaterialDto.setSize((int) lectureMaterial.getSize());
        lectureMaterialDto.setType(lectureMaterial.getType());
        lectureMaterialDto.setExternalLink(lectureMaterial.getExternalLink());
        lectureMaterialDto.setFilePath(lectureMaterial.getFilePath());
        lectureMaterialDto.setFileEx(lectureMaterial.getFileEx());
        lectureMaterialDto.setStoredType(lectureMaterial.getStoredType());
        lectureMaterialDto.setFileName(lectureMaterial.getFileName());
        return  lectureMaterialDto;
    }

}
