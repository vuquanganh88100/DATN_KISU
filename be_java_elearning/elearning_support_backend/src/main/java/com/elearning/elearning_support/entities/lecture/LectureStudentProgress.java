package com.elearning.elearning_support.entities.lecture;

import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student_lecture_progress", schema = "elearning_support_dev")
public class LectureStudentProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="max_watched_time")
    private Long maxWatchedTime;
    @Column(name="total_answer_correct")
    private Long totalAnswerCorrect;
    @Column(name="is_completed")
    private boolean isCompleted;
    @Column(name="last_updated")
    private Date lastUpdated;
    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User user;
            ;
}
