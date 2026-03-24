package com.elearning.elearning_support.entities.lecture;

import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lecture", schema = "elearning_support_dev")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id", nullable = false)
    private Long id;
    @OneToMany(mappedBy = "lecture")
    private List<LectureMaterial> lectureMaterials;
    @Column(name="lecture_name")
    private String lectureName;
    @Column(name="required_watch_time")
    private Integer requiredTime;
    @Column(name = "lecture_weight")
    private Integer lectureWeight;
    @Column(name = "sequence")
    private Integer sequence;
    @Column(name="total_question")
    private Integer totalQuestion;
    @Column(name="required_correct_ans")
    private Integer requiredCorrectAns;
    @Column(name = "video_duration")
    private  Integer videoDuration;
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
    @ManyToOne
    @JoinColumn(name = "online_course_id",nullable = false)
    private OnlineCourse onlineCourse;
    @OneToMany(mappedBy = "lecture")
    private List<LectureQuestion> lectureQuestions;
    @Column(name = "created_by")
    private Integer createdBy;
    @OneToMany(mappedBy = "lecture")
    private List<LectureStudentProgress> studentLectureProgresses;


}
