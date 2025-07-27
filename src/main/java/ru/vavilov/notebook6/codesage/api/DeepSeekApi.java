package ru.vavilov.notebook6.codesage.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.codesage.model.Recommendation;
import ru.vavilov.notebook6.codesage.model.RecommendationResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class DeepSeekApi {
    private static final String BASE_URL = "https://api.deepseek.com";
    private final String apiKey;
    private final OkHttpClient client;

    public DeepSeekApi(@Value("${deepseek.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    }

    public RecommendationResponse chatCompletion(JSONObject requestBody) {
        try {
            return post("/chat/completions", requestBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RecommendationResponse post(String endpoint, JSONObject jsonBody) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();

            JSONObject jsonResponse = new JSONObject(responseBody);

            return parseRecommendationResponse(jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content").toString());
        }
    }
    public RecommendationResponse parseRecommendationResponse(String rawContent) throws IOException {
        String cleaned = rawContent.replaceAll("(?s)```json\\s*|\\s*```", "").trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start == -1 || end == -1 || end < start) {
            throw new IOException("Некорректный JSON-формат в ответе ассистента");
        }
        String jsonOnly = cleaned.substring(start, end + 1);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(cleaned, RecommendationResponse.class);
        } catch (MismatchedInputException e) {
            Recommendation rec = mapper.readValue(cleaned, Recommendation.class);
            RecommendationResponse response = new RecommendationResponse();
            response.setRecommendations(Collections.singletonList(rec));
            return response;
        }
    }
}