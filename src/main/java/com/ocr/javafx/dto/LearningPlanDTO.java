package com.ocr.javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LearningPlanDTO {
    private Long id;
    private String title;
    private String domain;
    private String goal;
    private Integer progress;
    private Integer durationDays;
    private String intensity;
    private List<String> skills;
    private Integer remainingDays;
    private String startedDate;
}
