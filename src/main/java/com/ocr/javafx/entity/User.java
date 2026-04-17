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

    @Column(name = "total_hours", nullable = false)
    private int totalHours = 0;

    @Column(name = "completed_plans", nullable = false)
    private int completedPlans = 0;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "skills_learned", nullable = false)
    private int skillsLearned = 0;

}