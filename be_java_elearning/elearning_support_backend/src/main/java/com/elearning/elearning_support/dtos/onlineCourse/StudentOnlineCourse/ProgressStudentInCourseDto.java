package com.elearning.elearning_support.dtos.onlineCourse.StudentOnlineCourse;

import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.users.UserListDTO;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStudentInCourseDto {
    private String studentCode;
    private String lastName;
    private long userId;
    private String firstName;
    private double completedPercent;
    private OnlineCourseDto onlineCourseDto;
}
