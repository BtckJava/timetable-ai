package com.ocr.javafx.dto.request;

import com.ocr.javafx.enums.StudyIntensity;
import com.ocr.javafx.enums.TimePreference;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AIGenerateRequest {
    // Data mượn từ LearningPlan (bạn lấy từ object plan user chọn trên màn hình)
    private String domain;
    private String goal;
    private int durationDays;

    // Data do user chọn ở màn Timetable của bạn
    private StudyIntensity intensity;
    private TimePreference timePreference;

    // Danh sách lịch bận để AI né ra
    private List<BusySlotDTO> busySlots;
}