package com.ocr.javafx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.javafx.backend.dto.ScheduleSlotDto;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class BackendApiClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String baseUrl;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public BackendApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void generateSchedule(Long planId) throws IOException {
        String payload = "{\"planId\":" + planId + "}";
        Request request = new Request.Builder()
                .url(baseUrl + "/api/ai/generate")
                .post(RequestBody.create(payload, JSON))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Generate failed with code " + response.code());
            }
        }
    }

    public List<ScheduleSlotDto> getSchedule(Long planId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/schedule/" + planId)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Fetch schedule failed with code " + response.code());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<>() {});
        }
    }
}
