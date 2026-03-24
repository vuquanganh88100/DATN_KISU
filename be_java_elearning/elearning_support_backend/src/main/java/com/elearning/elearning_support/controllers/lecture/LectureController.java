package com.elearning.elearning_support.controllers.lecture;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.dtos.lecture.LectureProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureQuestionDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseLearningDetailDto;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.studentLecture.StudentLectureService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/online/course")
public class LectureController {
    @Autowired
    LectureService lectureService;
    @Autowired
    StudentLectureService studentLectureService;
    @PostMapping("/lecture")
    public ResponseEntity<String> addLecture(
            @RequestBody  LectureDto lectureDto
            ){
        lectureService.addAllInformationLecture(lectureDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lecture  added successfully");
    }
    @GetMapping("{onlineCourseId}/chapter/{chapterId}/lecture")
    public ResponseEntity<List<LectureDto>> getAllLectureByChapter(@PathVariable long onlineCourseId,@PathVariable  long chapterId){
      List<LectureDto>lectureDtos=lectureService.getAllLectureByChapter(chapterId,onlineCourseId);
        return new ResponseEntity<>(lectureDtos, new HttpHeaders(), HttpStatus.OK);
    }
    // ti phai sua lai thanh question chu ko phai lecture
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<LectureDto>getLectureDetail(@PathVariable long lectureId){
        LectureDto lectureDto=lectureService.getLectureDetail(lectureId);
        return new ResponseEntity<>(lectureDto, new HttpHeaders(), HttpStatus.OK);
    }
//    @GetMapping("/{onlineCourseId}/lecture")
//    public ResponseEntity<List<ChapterCourseDetailDto>>getAllLectureInCourse(@PathVariable long onlineCourseId){
//        List<ChapterCourseDetailDto> chapterCourseDetailDto=lectureService.getChapterCourseDetail(onlineCourseId);
//        return new ResponseEntity<>(chapterCourseDetailDto, new HttpHeaders(), HttpStatus.OK);
//    }
    @GetMapping("/lecture/progress/{userId}")
    public ResponseEntity< Map<Long,LectureProgressDto>>getInformationProgressLecture(@PathVariable long userId){
        Map<Long,LectureProgressDto> infomationLectureProgress=studentLectureService.getInfomationLectureProgress(userId);
        return new ResponseEntity<>(infomationLectureProgress, new HttpHeaders(), HttpStatus.OK);
    }
}
