package com.ocr.javafx.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiGenerateResponse {
    private Long planId;
    private int totalGenerated;
    private int totalSaved;
    private int totalSkippedConflict;
}
