package com.elearning.elearning_support.entities.resultTestCourse;

import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "result_test_course", schema = "elearning_support_dev")
public class ResultTestCourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "test_course_id", nullable = false)
    private TestCourseEntity testCourseEntity;
    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;
    @Column(name="submit_time")
    private Date submitTime;
    @OneToMany(mappedBy = "resultTestCourse")
    private List<ResultTestCourseDetail> resultTestCourseDetailList;
    @Column(name = "total_time_to_do")
    private Long totalTimeToDo;
    @Column(name="student_point")
    private Double studentPoint;

}
