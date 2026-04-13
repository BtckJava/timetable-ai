package com.ocr.javafx.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AIService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public AIService(ObjectMapper objectMapper,
                     @Value("${openrouter.api.key}") String apiKey,
                     @Value("${openrouter.model}") String model) {
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateScheduleJson(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing OPENROUTER_API_KEY");
        }
        try {
            String body = objectMapper.createObjectNode()
                    .put("model", model)
                    .set("messages", objectMapper.createArrayNode()
                            .add(objectMapper.createObjectNode().put("role", "user").put("content", prompt)))
                    .toString();

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body, JSON))
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IllegalStateException("OpenRouter error: " + response.code());
                }
                String raw = response.body().string();
                JsonNode root = objectMapper.readTree(raw);
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                String content = contentNode.asText("");
                if (content.isBlank()) {
                    throw new IllegalStateException("OpenRouter returned empty content");
                }
                return extractJson(content);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to call OpenRouter", e);
        }
    }

    private String extractJson(String content) {
        String clean = content.trim()
                .replace("```json", "")
                .replace("```", "")
                .trim();
        int firstBrace = clean.indexOf('{');
        int lastBrace = clean.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return clean.substring(firstBrace, lastBrace + 1);
        }
        return clean;
    }
}
