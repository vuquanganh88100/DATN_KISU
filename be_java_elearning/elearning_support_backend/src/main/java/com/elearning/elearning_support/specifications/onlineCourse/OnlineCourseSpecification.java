package com.elearning.elearning_support.specifications.onlineCourse;

import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.subject.Subject;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Subgraph;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OnlineCourseSpecification {
    public static Specification<OnlineCourse> findOnlineCourse(String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                Predicate courseNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("courseName")),
                        "%" + keyword.toLowerCase() + "%"
                );
                Join<OnlineCourse, Subject> subjectJoin = root.join("subject");
                Predicate subjectCodePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(subjectJoin.get("code")),
                        "%" + keyword.toLowerCase() + "%"
                );
                predicates.add(criteriaBuilder.or(courseNamePredicate, subjectCodePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

//    public static Specification<Subject> findBySubjectCode(String subjectCode){
//        return (root, query, criteriaBuilder) -> {
//            List<Predicate>predicates=new ArrayList<>();
//            if(subjectCode!=null){
//                predicates.add(criteriaBuilder.equal(root.get("code"),subjectCode));
//            }
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0])) ;
//        };
//    }
}
