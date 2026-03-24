package com.elearning.elearning_support.entities.onlineCourse;

import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.users.User;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "online_course_chapter_weight", schema = "elearning_support_dev")
public class OnlineCourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,nullable = false)
    private Long id;

    @Column(name = "chapter_weight")
    private Integer chapterWeight;

    @ManyToOne
    @JoinColumn(name = "online_course_id", nullable = false)
    private OnlineCourse onlineCourse;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
}
