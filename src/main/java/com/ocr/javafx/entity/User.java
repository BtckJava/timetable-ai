package com.ocr.javafx.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "total_hours")
    private int totalHours;

    @Column(name = "completed_plans")
    private int completedPlans;

    @Column(name = "current_streak")
    private int currentStreak;

    @Column(name = "skills_learned")
    private int skillsLearned;

}