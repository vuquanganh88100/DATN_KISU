package com.elearning.elearning_support.controllers.onlineCourse;

import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.onlineCourse.StudentOnlineCourse.ProgressStudentInCourseDto;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.onlineCourse.OnlineCourseChapterService;
import com.elearning.elearning_support.services.onlineCourse.OnlineCourseService;
import com.elearning.elearning_support.services.studentOnlineCourse.StudentOnlineCourseService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/online/course")
public class OnlineCourseController {
    @Autowired
    private OnlineCourseService onlineCourseService;
    @Autowired
    private StudentOnlineCourseService studentOnlineCourseService;
    @Autowired
    private OnlineCourseChapterService onlineCourseChapterService;
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> add(
            @RequestParam("fileCourseImg") MultipartFile fileCourseImg,
            @RequestParam("subjectId") int subjectId,
            @RequestParam("courseName") String courseName,
            @RequestParam("courseDescription") String courseDescription,
            @RequestParam("teacherId") List<Long> teacherId,
            @RequestParam("onlineCourseChaptersDto") String onlineCourseChaptersDtoJson) {

        OnlineCourseDto onlineCourseDto = new OnlineCourseDto();
        onlineCourseDto.setFileCourseImg(fileCourseImg);
        onlineCourseDto.setSubjectId(subjectId);
        onlineCourseDto.setCourseName(courseName);
        onlineCourseDto.setCourseDescription(courseDescription);
        onlineCourseDto.setTeacherId(teacherId);
        onlineCourseDto.setPublish(false);

        ObjectMapper objectMapper = new ObjectMapper();
        List<OnlineCourseChapterDto> onlineCourseChaptersDto;
        try {
            onlineCourseChaptersDto = objectMapper.readValue(onlineCourseChaptersDtoJson,
                    new TypeReference<List<OnlineCourseChapterDto>>() {
                    });
            onlineCourseDto.setOnlineCourseChaptersDto(onlineCourseChaptersDto);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format for chapters");
        }

         onlineCourseService.addOnlineCourse(onlineCourseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Online course added successfully");
    }
    @GetMapping(value = "/page")
    public ResponseEntity<Page<OnlineCourseDto>> getListChapter(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<OnlineCourseDto> courses = onlineCourseService.getListCourse(pageable); // Lấy kết quả phân trang
        return new ResponseEntity<>(courses, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping(value = "{id}")
    public ResponseEntity<OnlineCourseDto> getCourseById(@PathVariable Long id) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // Lấy toàn bộ danh sách
        OnlineCourseDto course = onlineCourseService.getCourseById(id, pageable);
        return new ResponseEntity<>(course, new HttpHeaders(), HttpStatus.OK);
    }
    @PutMapping(value = "/{onlineCourseId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@PathVariable(name = "onlineCourseId") Long onlineCourseId,
                                         @RequestParam("fileCourseImg") MultipartFile fileCourseImg,
                                         @RequestParam("subjectId") int subjectId,
                                         @RequestParam("courseName") String courseName,
                                         @RequestParam("courseDescription") String courseDescription,
                                         @RequestParam("teacherId") List<Long> teacherId,
                                         @RequestParam("onlineCourseChaptersDto") String onlineCourseChaptersDtoJson){
        OnlineCourseDto onlineCourseDto = new OnlineCourseDto();
        onlineCourseDto.setFileCourseImg(fileCourseImg);
        onlineCourseDto.setSubjectId(subjectId);
        onlineCourseDto.setCourseName(courseName);
        onlineCourseDto.setCourseDescription(courseDescription);
        onlineCourseDto.setTeacherId(teacherId);

        ObjectMapper objectMapper = new ObjectMapper();
        List<OnlineCourseChapterDto> onlineCourseChaptersDto;
        try {
            onlineCourseChaptersDto = objectMapper.readValue(onlineCourseChaptersDtoJson,
                    new TypeReference<List<OnlineCourseChapterDto>>() {
                    });
            onlineCourseDto.setOnlineCourseChaptersDto(onlineCourseChaptersDto);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format for chapters");
        }

        onlineCourseService.updateOnlineCourse(onlineCourseId,onlineCourseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Online course updated successfully");
    }
    @PutMapping("/{courseId}/publish")
    public ResponseEntity<String> updatePublishStatus(@PathVariable Long courseId, @RequestBody Map<String,Boolean>request) {
        boolean isPublish=request.get("publish");
        onlineCourseService.updatePublish(courseId,isPublish);
        return ResponseEntity.ok("Update publish successfully");
    }
    @GetMapping("detail/{onlineCourseId}")
    public ResponseEntity<List<ChapterCourseDetailDto>>getLecturesAndTestInCourse(@PathVariable long onlineCourseId){
        List<ChapterCourseDetailDto> chapterCourseDetailDto=onlineCourseChapterService.getChapterCourseDetail(onlineCourseId);
        return new ResponseEntity<>(chapterCourseDetailDto, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping("/statistic/{onlineCourseId}")
    public ResponseEntity<List<ProgressStudentInCourseDto>> statisticCourse(@PathVariable long onlineCourseId) {
        List<ProgressStudentInCourseDto>progressStudentInCourseDtos=studentOnlineCourseService.getListProgressOfStudent(onlineCourseId);
        return ResponseEntity.ok(progressStudentInCourseDtos);
    }


}
