package com.ocr.javafx.service;

import com.ocr.javafx.config.AppEnv;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenRouterAI {

    private static String resolveApiKey() {
        String k = AppEnv.get("OPENROUTER_API_KEY");
        if (k == null) {
            k = AppEnv.get("API_KEY");
        }
        return k;
    }

    public static String ask(String prompt) throws Exception {
        String apiKey = "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";
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Thiếu OPENROUTER_API_KEY hoặc API_KEY trong .env / biến môi trường (xem .env.example).");
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject bodyJson = new JSONObject();

        bodyJson.put("model", "google/gemma-3n-e2b-it:free");

        JSONArray messages = new JSONArray();

        JSONObject msg = new JSONObject();
        msg.put("role", "user");
        msg.put("content", prompt);

        messages.put(msg);

        bodyJson.put("messages", messages);

        RequestBody body = RequestBody.create(
                bodyJson.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "http://localhost")
                .addHeader("X-Title", "JavaFX AI App")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        String responseStr = response.body().string();

        System.out.println("Response: " + responseStr);

        JSONObject result = new JSONObject(responseStr);

        if (!result.has("choices")) {
            return "API Error: " + responseStr;
        }

        return result
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
