package com.ocr.javafx.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LearningSession {
    private int userId;
    private LocalDate date;
    private double duration; // hours

}
