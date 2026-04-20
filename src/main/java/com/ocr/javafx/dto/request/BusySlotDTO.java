package com.ocr.javafx.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class BusySlotDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}