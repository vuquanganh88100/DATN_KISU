package com.elearning.elearning_support.entities.onlineCourse;

import com.elearning.elearning_support.entities.BaseEntity;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_online_course", schema = "elearning_support_dev")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOnlineCourse  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_type")
    private String roleType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "online_course_id", nullable = false)
    private OnlineCourse onlineCourse;

}
