package com.ocr.javafx.service;

import com.ocr.javafx.repository.ScheduleSlotRepository;

public class ScheduleSlotService {
    private final ScheduleSlotRepository repository;

    public ScheduleSlotService(ScheduleSlotRepository repository) {
        this.repository = repository;
    }
}
