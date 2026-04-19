package com.ocr.javafx.dto.response;

import com.ocr.javafx.dto.LearningPlanDTO;

import java.util.List;

public class LearningPlanResponse {
    private boolean success;
    private String response;
    private List<LearningPlanDTO> data;
    public LearningPlanResponse(boolean success, String response) {
        this.success = success;
        this.response = response;
    }
    public LearningPlanResponse(boolean success, String response, List<LearningPlanDTO> data) {
        this.success = success;
        this.response = response;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponse() {
        return response;
    }

    public List<LearningPlanDTO> getData() {
        return data;
    }
}
