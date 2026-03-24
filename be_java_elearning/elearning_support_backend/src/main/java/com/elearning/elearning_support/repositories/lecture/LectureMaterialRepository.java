package com.elearning.elearning_support.repositories.lecture;

import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial,Long> {
    @Query(value = "SELECT * FROM lecture_material WHERE lecture_id = :lectureId", nativeQuery = true)
    List<LectureMaterial> findAllByLecture(@Param("lectureId") Long lectureId);
}
