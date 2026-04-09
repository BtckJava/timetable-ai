package com.ocr.javafx.dto.response;

import lombok.Getter;

@Getter
public class LearningHoursResponse {
    private String day;
    private double hours;

    public LearningHoursResponse(String day, double hours){
        this.day = day;
        this.hours = hours;
    }
}
