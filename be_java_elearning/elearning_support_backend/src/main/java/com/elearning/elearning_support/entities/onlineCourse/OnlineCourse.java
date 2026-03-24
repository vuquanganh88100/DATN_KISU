package com.elearning.elearning_support.entities.onlineCourse;

import com.elearning.elearning_support.entities.BaseEntity;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.subject.Subject;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "online_course", schema = "elearning_support_dev")
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
public class OnlineCourse   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    @Column(name = "createdBy", nullable = false)
    private Long createdBy;
    @Column(name = "course_name", nullable = false, length = 255)
    private String courseName;
    @Column(name = "course_urlimg", nullable = false, length = 255)
    private String courseUrlImg;
    @Column(name = "course_description")
    @Type(type = "org.hibernate.type.TextType")
    private String courseDescription;
    @OneToMany(mappedBy = "onlineCourse")
    private List<UserOnlineCourse> userOnlineCourseList;

    @OneToMany(mappedBy = "onlineCourse")
    private List<OnlineCourseChapter> onlineCourseChapters;
    @OneToMany(mappedBy = "onlineCourse")
    private List<Lecture>lectures;

    @Column(name="is_publish")
    private boolean publish;
    @OneToMany(mappedBy = "onlineCourse")
    private List<TestCourseEntity> testCourseEntities;
}
