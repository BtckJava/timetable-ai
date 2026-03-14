package com.ocr.javafx.dto;

public class ScheduleSlotDTO {

    private String date;
    private String startTime;
    private String endTime;
    private String topic;
    private String subTopic;
    private String resourceUrl;

    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTopic() { return topic; }
    public String getSubTopic() { return subTopic; }
    public String getResourceUrl() { return resourceUrl; }

    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
}
