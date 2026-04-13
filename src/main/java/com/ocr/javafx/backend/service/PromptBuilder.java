package com.ocr.javafx.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.javafx.backend.dto.ScheduleSlotDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    private final ObjectMapper objectMapper;

    public PromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String build(String domain,
                        String goal,
                        String level,
                        Integer durationDays,
                        int sessionsPerDay,
                        List<ScheduleSlotDto> existingSchedule) {
        String existingScheduleJson = toJson(existingSchedule);
        return """
                You are an AI timetable planner. Generate a study schedule in strict JSON only.

                Constraints:
                - Domain: %s
                - Goal: %s
                - Level: %s
                - DurationDays: %d
                - SessionsPerDay: %d
                - Generate between 3 and 4 sessions per day.
                - Do not generate overlapping sessions.
                - Must avoid overlap with ExistingSchedule.
                - Time format HH:mm, Date format yyyy-MM-dd.
                - resourceUrl must not be a YouTube link.

                ExistingSchedule (last 60 days):
                %s

                Output format (JSON only, no markdown):
                {
                  "scheduleSlots": [
                    {
                      "date": "2026-03-14",
                      "startTime": "08:00",
                      "endTime": "09:30",
                      "topic": "JavaScript",
                      "subTopic": "Loops",
                      "resourceUrl": "https://..."
                    }
                  ]
                }
                """.formatted(domain, goal, level, durationDays, sessionsPerDay, existingScheduleJson);
    }

    private String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
