package com.ocr.javafx.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "learning_plans")
@Getter
@Setter
public class LearningPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String domain;

    @Column(columnDefinition = "TEXT")
    private String goal;

    private Integer progress;

    private Integer durationDays;

    private String intensity;

    @ElementCollection
    @CollectionTable(name = "learning_plan_skills", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "skill")
    private List<String> skills;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date ASC, startTime ASC")
    private List<ScheduleSlot> slots;

}
