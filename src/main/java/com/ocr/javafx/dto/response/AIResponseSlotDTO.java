package com.ocr.javafx.dto.response;

import lombok.Data;

@Data
public class AIResponseSlotDTO {
    private String date;       // Format: YYYY-MM-DD
    private String startTime;  // Format: HH:mm
    private String endTime;    // Format: HH:mm
    private String topic;
    private String subTopic;
    private String resourceUrl;
}