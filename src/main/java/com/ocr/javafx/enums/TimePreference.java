package com.ocr.javafx.enums;

public enum TimePreference {
    MORNING("06:00", "11:00", "Chỉ học buổi sáng"),
    AFTERNOON("13:00", "17:00", "Chỉ học buổi chiều"),
    EVENING("19:00", "23:00", "Chỉ học buổi tối"),
    FLEXIBLE("06:00", "23:00", "Linh hoạt cả ngày");

    private final String startTime;
    private final String endTime;
    private final String displayValue;

    TimePreference(String startTime, String endTime, String displayValue) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayValue = displayValue;
    }

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    @Override
    public String toString() { return displayValue; }
}