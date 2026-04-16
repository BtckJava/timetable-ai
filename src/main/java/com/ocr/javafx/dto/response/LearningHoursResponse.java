package com.ocr.javafx.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LearningHoursResponse {
    private LocalDate localDate;
    private final double hours;

    public LearningHoursResponse(LocalDate localDate, double hours){
        this.localDate = localDate;
        this.hours = hours;
    }
}
