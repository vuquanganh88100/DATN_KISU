package com.elearning.elearning_support.controllers.onlineCourse.studentOnlineCourse;

import com.elearning.elearning_support.dtos.lecture.LearningLectureStudentProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureStudentProgressDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseLearningDetailDto;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.onlineCourse.OnlineCourseService;
import com.elearning.elearning_support.services.studentLecture.StudentLectureService;
import com.elearning.elearning_support.services.studentOnlineCourse.StudentOnlineCourseService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import com.elearning.elearning_support.utils.pdfFile.LectureQuestionPdfExported;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/online/student-course")
public class StudentOnlineCourseController {
    @Autowired
    StudentOnlineCourseService studentOnlineCourseService;
    @Autowired
    StudentLectureService studentLectureService;
    @Autowired
    LectureService lectureService;
    @GetMapping("/page")
    public ResponseEntity<Page<OnlineCourseDto>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OnlineCourseDto> courses = studentOnlineCourseService.getListCourseCustom(pageable, keyword);
        return ResponseEntity.ok(courses);
    }
    @GetMapping(value = "{id}")
    public ResponseEntity<OnlineCourseDetailDto> getCourseDetailsById(@PathVariable Long id) {
//        OnlineCourseDto courseInfo = studentLectureService.getStudentCourseById(id);
//        Map<String, List<LectureDto>> chapterLectureMap = studentLectureService.getChapterByCourse(id);

//        OnlineCourseDetailDto response = new OnlineCourseDetailDto(courseInfo, chapterLectureMap);
        OnlineCourseDetailDto onlineCourseDetailDto=studentOnlineCourseService.getCourseDetail(id);
        return new ResponseEntity<>(onlineCourseDetailDto, new HttpHeaders(), HttpStatus.OK);
    }
    @PostMapping("enroll")
    public ResponseEntity<String> enrollCourse(@RequestBody Map<String, Long> payload) {
        studentOnlineCourseService.enrollCourse(payload.get("courseId"));
        return ResponseEntity.ok("Enroll successfully!");
    }
    @GetMapping(value = "/learning/{courseId}")
    public ResponseEntity<?> getStudentCourseLearning(@PathVariable Long courseId) {
//        boolean isEnrolled = studentOnlineCourseService.checkEnrolled(courseId);
//        if (!isEnrolled) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Bạn cần đăng ký khóa học để truy cập");
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
//        } else {
        long userId=AuthUtils.getCurrentUserId();
            OnlineCourseLearningDetailDto onlineCourseLearningDetailDto = studentLectureService.getLearningCourseDetail(courseId);
            onlineCourseLearningDetailDto.setTotalCompletetionPercent(studentOnlineCourseService.getPercentCompleted(userId,courseId));
            return ResponseEntity.ok(onlineCourseLearningDetailDto);
//        }
    }
    @GetMapping(value = "/learning/{courseId}/lecture/{lectureId}")
    public ResponseEntity<?> getStudentCourseProgress(@PathVariable Long courseId,
                                                      @PathVariable Long lectureId
                                                     )
    {
        boolean isEnrolled = studentOnlineCourseService.checkEnrolled(courseId);
        if (!isEnrolled) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bạn cần đăng ký khóa học để truy cập");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } else {
            LearningLectureStudentProgressDto learningLectureStudentProgressDto=studentLectureService.getLearningLectureDetail(lectureId);

            return ResponseEntity.ok(learningLectureStudentProgressDto);
        }
    }
    //api save time
    @PostMapping("/learning/{lectureId}/saveTime")
    public ResponseEntity<LectureStudentProgressDto> saveTime(@PathVariable long lectureId,
                         @RequestBody Map<String,Long>requestBody){
        Long time=requestBody.get("timeProgress");
        LectureStudentProgressDto dto= studentLectureService.saveTime(time, AuthUtils.getCurrentUserId(),lectureId);
        return ResponseEntity.ok(dto);

    }

    // api save question
    @PostMapping("/learning/{lectureId}/saveQuestion")
    public ResponseEntity<LectureStudentProgressDto> saveQuestion(@PathVariable long lectureId,
                             @RequestBody Map<String, Boolean> requestBody) {
        boolean studentAns = requestBody.get("studentAns");
        LectureStudentProgressDto dto=studentLectureService.saveTotalAnsCorrect(studentAns, AuthUtils.getCurrentUserId(), lectureId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/learning/{lectureId}/reset")
    public ResponseEntity<?> resetProgress(@PathVariable long lectureId
                             ){
        studentLectureService.resetProgress(AuthUtils.getCurrentUserId(),lectureId);
        return ResponseEntity.ok("reset successfully");
    }
    @GetMapping("/learning/{lectureId}/checkStatus")
    public ResponseEntity<LectureStudentProgressDto> check(@PathVariable long lectureId
    ){
     LectureStudentProgressDto dto=studentLectureService.checkStatus(AuthUtils.getCurrentUserId(),lectureId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping(value = "/learning")
    public ResponseEntity<?> getMytStudentCoursesLearning() {
            List<OnlineCourseDetailDto> onlineCourseDetailDtos=studentOnlineCourseService.getAllCourseRegistedByStudent();
            return ResponseEntity.ok(onlineCourseDetailDtos);
    }
    @GetMapping(value = "/export-questions/{lectureId}")
    public void exportQuestionlecture(@PathVariable long lectureId, HttpServletResponse response) throws IOException {
        QuestionListResDTO questionListResDTO = studentLectureService.exportLectureQuestion(lectureId);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=questions.pdf");
        LectureQuestionPdfExported.exportLectureQuestionPdf(questionListResDTO, response.getOutputStream());
        response.flushBuffer();
    }
}
