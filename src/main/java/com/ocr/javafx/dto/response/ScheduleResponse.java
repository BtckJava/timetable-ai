package com.ocr.javafx.dto.response;

import com.ocr.javafx.dto.ScheduleSlotDTO;

import java.util.List;

public class ScheduleResponse {
    private List<ScheduleSlotDTO> scheduleSlots;

    public List<ScheduleSlotDTO> getScheduleSlots() {
        return scheduleSlots;
    }

    public void setScheduleSlots(List<ScheduleSlotDTO> scheduleSlots) {
        this.scheduleSlots = scheduleSlots;
    }
}
