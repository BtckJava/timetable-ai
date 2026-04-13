package com.ocr.javafx.backend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiScheduleSlotPayload {
    private String date;
    private String startTime;
    private String endTime;
    private String topic;
    private String subTopic;
    private String resourceUrl;
}
