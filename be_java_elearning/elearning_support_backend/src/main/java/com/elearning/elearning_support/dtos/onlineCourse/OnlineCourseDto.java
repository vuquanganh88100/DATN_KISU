package com.elearning.elearning_support.dtos.onlineCourse;

import com.elearning.elearning_support.dtos.combo.DepartmentAndSubjectDto;
import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCourseDto {
    private int courseId;
    private int subjectId;
    private String subjectCode;
    private String courseName;
    private String courseUrlImg;
    private MultipartFile fileCourseImg;
    private String publicId;

    private String courseDescription;
    private int createdBy;
    private List<Long> teacherId;
    private List<String>teacherName;
    private List<OnlineCourseChapterDto> onlineCourseChaptersDto;
    private  String subjectName;
    private String departmentName;
    private  boolean publish;
    private boolean isStudentEnrolled;
    private  long totalStudentEnrolled;
    private Timestamp lastTimeToLearn;

}
