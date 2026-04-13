package com.ocr.javafx.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlanRequest {
    private Long userId;
    private String domain;
    private String goal;
    private String level;
    private Integer durationDays;
}
