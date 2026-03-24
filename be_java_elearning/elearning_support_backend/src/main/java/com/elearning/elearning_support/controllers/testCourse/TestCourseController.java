package com.elearning.elearning_support.controllers.testCourse;

import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseResDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import com.elearning.elearning_support.services.resultTestCourseService.ResultTestCourseService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import com.elearning.elearning_support.utils.pdfFile.LectureQuestionPdfExported;
import com.elearning.elearning_support.utils.pdfFile.TestCourseQuestionPdfExported;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/online/course")
public class TestCourseController {
    @Autowired
    TestCourseService testCourseService;
    @Autowired
    ResultTestCourseService resultTestCourseService;
    @PostMapping("/create-test")
    public ResponseEntity<String>addTestCourse( @RequestBody TestCourseDto testCourseDto){
        testCourseService.addTestCourse(testCourseDto);
        return ResponseEntity.ok("add test course successfully");
    }
    @GetMapping("/test/{id}")
    public ResponseEntity<?>getOverviewTestCourse(@PathVariable long id){
        TestCourseDto testCourseDto=testCourseService.getTestCourseOverview(id);
        return ResponseEntity.ok(testCourseDto);
    }
    @GetMapping("/detail-test/{testId}")
    public ResponseEntity<?>getDetailTestCourse(@PathVariable long testId){
        TestCourseDto testCourseDto=testCourseService.getDetailTestCourse(testId);
        return  ResponseEntity.ok(testCourseDto);
    }
    @PostMapping("student-test/save/ans")
        public ResponseEntity<?> saveAns(@RequestBody ResultTestCourseDto dto){
            resultTestCourseService.saveResultTestCourse(dto);
            ResultTestCourseResDto resDto= resultTestCourseService.getResStudentTest(dto.getTestCourseId());
        return new ResponseEntity<>(resDto, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping("/student-test/result/{testCourseId}")
    public ResponseEntity<?> getResult(@PathVariable long testCourseId){
        ResultTestCourseResDto resDto= resultTestCourseService.getResStudentTest(testCourseId);
        return new ResponseEntity<>(resDto, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping(value = "/export/test-course/{testCourseId}")
    public void exportQuestionlecture(@PathVariable long testCourseId, HttpServletResponse response) throws IOException {
        ResultTestCourseResDto resDto= resultTestCourseService.getResStudentTest(testCourseId);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=questions.pdf");
        TestCourseQuestionPdfExported.exportTestCourseToPdf(resDto, response.getOutputStream());
        response.flushBuffer();
    }

}
