package com.elearning.elearning_support.entities.contest;

import com.elearning.elearning_support.entities.lecture.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "topic_contest")
public class TopicContest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "topicContest")
    private List<ProblemContest> problemContests;

}
