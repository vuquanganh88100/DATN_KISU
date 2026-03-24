package com.elearning.elearning_support.services.studentOnlineCourse;

import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseLearningDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.StudentOnlineCourse.ProgressStudentInCourseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentOnlineCourseService {
    Page<OnlineCourseDto> getListCourseCustom(Pageable pageable, String keyword);
    OnlineCourseDto getCourseWithoutEnroll(long courseId,Pageable pageable, String keyword);
    void enrollCourse(long courseId);
    boolean checkEnrolled(long courseId);
    double getPercentCompleted(long userId,long onlineCourseId);
    List<ProgressStudentInCourseDto>getListProgressOfStudent(long onlineCourseId);
//    List<OnlineCourseLearningDetailDto> getAllCourseRegistedByStudent();
        List<OnlineCourseDetailDto> getAllCourseRegistedByStudent();

        OnlineCourseDto getStudentCourseById(Long courseId);

    OnlineCourseDetailDto getCourseDetail(long courseId);
    OnlineCourseDto getStudentCourseByIdNew(long currentUserId,Long courseId);

}
