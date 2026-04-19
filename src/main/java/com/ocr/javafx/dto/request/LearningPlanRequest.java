package com.ocr.javafx.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningPlanRequest {
    private String title;
    private String goal;
    private String intensity;
    private List<String> skills;
    private String domain;
    private Integer durationDays;
}
