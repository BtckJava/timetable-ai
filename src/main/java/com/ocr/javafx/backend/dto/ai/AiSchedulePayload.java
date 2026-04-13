package com.ocr.javafx.backend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiSchedulePayload {
    private List<AiScheduleSlotPayload> scheduleSlots = new ArrayList<>();
}
