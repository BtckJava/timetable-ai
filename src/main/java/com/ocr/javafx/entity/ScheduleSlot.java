package com.ocr.javafx.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_slots")
@Getter
@Setter
public class ScheduleSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String topic;

    private String subTopic;

    private String resourceUrl;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = true)
    private LearningPlan plan;

}