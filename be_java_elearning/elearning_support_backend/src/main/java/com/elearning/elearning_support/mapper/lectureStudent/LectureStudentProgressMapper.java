package com.elearning.elearning_support.mapper.lectureStudent;

import com.elearning.elearning_support.dtos.lecture.LectureStudentProgressDto;
import com.elearning.elearning_support.entities.lecture.LectureStudentProgress;
import com.elearning.elearning_support.repositories.lecture.LectureRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class LectureStudentProgressMapper {
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    UserRepository userRepository;
//    public LectureStudentProgress toEntity(LectureStudentProgressDto dto){
//        LectureStudentProgress entity=new LectureStudentProgress();
//        entity.setLecture(lectureRepository.findById(dto.getLectureId()).get());
//        entity.setUser(userRepository.findById(dto.getStudentId()).get());
//        entity.setMaxWatchedTime(dto.getMaxWatchedTime());
//        entity.setTotalAnswerCorrect(dto.getTotalAnsCorrect());
//        entity.set
//    }
    public LectureStudentProgressDto toDto(LectureStudentProgress entity){
        LectureStudentProgressDto dto=new LectureStudentProgressDto();
        dto.setLectureId(entity.getLecture().getId());
        dto.setStudentId(entity.getUser().getId());
        dto.setCompleted(entity.isCompleted());
        dto.setTotalAnsCorrect(entity.getTotalAnswerCorrect());
        dto.setMaxWatchedTime(entity.getMaxWatchedTime());
        return dto;
    }
}
