package com.elearning.elearning_support.entities.lecture;

import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.question.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lecture_question", schema = "elearning_support_dev")
public class LectureQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "time_start")
    private Integer timeStart;
    @Column(name = "time_end")
    private Integer timeEnd;
    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;
    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;
}
