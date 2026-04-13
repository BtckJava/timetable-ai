package com.ocr.javafx.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyPlanResponse {
    private Long id;
    private Long userId;
    private String domain;
    private String goal;
    private String level;
    private Integer durationDays;
}
