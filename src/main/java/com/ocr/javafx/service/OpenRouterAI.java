package com.ocr.javafx.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenRouterAI {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    public static String ask(String prompt) throws Exception {

        OkHttpClient client = new OkHttpClient();

        JSONObject bodyJson = new JSONObject();

        bodyJson.put("model", "arcee-ai/trinity-large-preview:free");

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
                .addHeader("Authorization", "Bearer " + API_KEY)
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