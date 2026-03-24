package com.elearning.elearning_support.mapper.lectureMaterial;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class LectureMapper {
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    OnlineCourseRepository onlineCourseRepository;
    public  Lecture  toEntity(LectureDto lectureDto){
        Lecture lecture=new Lecture();
        lecture.setLectureName(lectureDto.getLectureName());
        lecture.setRequiredTime(lectureDto.getRequiredTime());
        lecture.setLectureWeight(lectureDto.getLectureWeight());
        lecture.setSequence(lectureDto.getSequence());
        lecture.setTotalQuestion(lectureDto.getTotalQuestion());
        lecture.setRequiredCorrectAns(lectureDto.getRequiredCorrectAns());
        lecture.setVideoDuration(lectureDto.getVideoDuration());
        lecture.setCreatedBy(Math.toIntExact(AuthUtils.getCurrentUserId()));

        // save chapter

        return lecture;

    }
    public LectureDto toDto(Lecture lecture){
        LectureDto lectureDto=new LectureDto();
        lectureDto.setId(lecture.getId());
        lectureDto.setLectureName(lecture.getLectureName());
        lectureDto.setRequiredTime(lecture.getRequiredTime());
        lectureDto.setSequence(lecture.getSequence());
        lectureDto.setTotalQuestion(lecture.getTotalQuestion());
        lectureDto.setRequiredCorrectAns(lecture.getRequiredCorrectAns());
        lectureDto.setLectureWeight(lecture.getLectureWeight());
        lectureDto.setVideoDuration(lecture.getVideoDuration());
        lectureDto.setRequiredTime(lecture.getRequiredTime());
        lectureDto.setChapterId(lecture.getChapter().getId());
        lectureDto.setOnlineCourseId(lecture.getOnlineCourse().getId());
        return  lectureDto;
    }
}
