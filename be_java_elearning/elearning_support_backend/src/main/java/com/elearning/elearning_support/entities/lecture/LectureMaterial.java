package com.elearning.elearning_support.entities.lecture;

import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lecture_material", schema = "elearning_support_dev")
public class LectureMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="created_by")
    private long created_by;
    @Column(name = "type")
    private int type;
    @Column(name = "file_ex")
    private String fileEx;
    @Column(name="size")
    private long size;
    @Column(name="stored_type")
    private int storedType;
    @Column(name="file_path")
    private String filePath;
    @Column(name="external_link")
    private String externalLink;
    @Column(name="file_name")
    private String fileName;
    @ManyToOne
    @JoinColumn(name="lecture_id")
    private Lecture lecture ;
}
