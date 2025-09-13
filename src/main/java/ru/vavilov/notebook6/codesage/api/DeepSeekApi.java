package ru.vavilov.notebook6.codesage.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
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
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeepSeekApi {
    private static final String BASE_URL = "https://api.deepseek.com";
    private final String apiKey;
    private final OkHttpClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekApi(@Value("${deepseek.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)
            .build();
    }

    public RecommendationResponse chatCompletion(JSONObject requestBody) {
        try {
            return post("/chat/completions", requestBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SubtitleEntry chatCompletionTranslator(List<JSONObject> requestBody, SubtitleEntry subtitleEntry) {
        try {
            return postMovieParallel("/chat/completions", requestBody, subtitleEntry);
        } catch (IOException | InterruptedException e) {
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

    public SubtitleEntry postMovieParallel(String endpoint, List<JSONObject> requests, SubtitleEntry subtitleEntry)
        throws IOException, InterruptedException {

        List<String>[] translatedBatches = new List[requests.size()];
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(requests.size());

        for (int i = 0; i < requests.size(); i++) {
            final int batchIndex = i;
            JSONObject jsonBody = requests.get(i);

            executor.submit(() -> {
                String responseBody = "";
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

                    Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            responseBody = response.body() != null ? response.body().string() : "";
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            String content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                            String cleanedContent = validateAndCleanJson(content);
                            List<String> translatedBatch = objectMapper.readValue(cleanedContent,
                                new TypeReference<List<String>>() {});

                            translatedBatches[batchIndex] = translatedBatch;

                            System.out.printf("Completed batch %d/%d%n", batchIndex + 1, requests.size());
                        } else {
                            throw new RuntimeException("request failed - " + responseBody);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error in batch " + batchIndex + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        if (!latch.await(30, TimeUnit.MINUTES)) {
            throw new IOException("Timeout waiting for batch completion");
        }

        executor.shutdown();

        List<String> allTranslatedTexts = new ArrayList<>();
        for (List<String> batch : translatedBatches) {
            if (batch != null) {
                allTranslatedTexts.addAll(batch);
            }
        }

        return subtitleEntry.setSubtitlesByTranslatedArray(allTranslatedTexts);
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

    private String validateAndCleanJson(String content) throws IOException {
        String cleaned = content.replace("```json", "")
            .replace("```", "")
            .trim();

        try {
            new ObjectMapper().readTree(cleaned);
            return cleaned;
        } catch (JsonProcessingException e) {
            return cleanInvalidJson(cleaned);
        }
    }

    private String cleanInvalidJson(String content) throws IOException {
        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        boolean inArray = false;
        boolean inString = false;
        boolean escapeNext = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            if (!inArray && line.startsWith("[")) {
                inArray = true;
                result.append("[\n");
                continue;
            }

            if (inArray) {
                if (line.startsWith("\"") || (inString && !line.startsWith("]"))) {
                    if (!inString) {
                        inString = true;
                        result.append("  ");
                    }

                    String cleanedLine = line.replaceAll("^\\.+", "")
                        .replaceAll("\\.+$", "");

                    result.append(cleanedLine);

                    if (cleanedLine.endsWith("\"") && !cleanedLine.endsWith("\\\"")) {
                        inString = false;
                        if (i < lines.length - 1 && !lines[i + 1].trim().startsWith("]")) {
                            result.append(",");
                        }
                        result.append("\n");
                    } else {
                        result.append(" ");
                    }
                } else if (line.startsWith("]")) {
                    inArray = false;
                    result.append("]\n");
                } else if (line.startsWith(",")) {
                    continue;
                } else {
                    if (!inString) {
                        result.append("  \"");
                        inString = true;
                    }

                    String cleanedLine = line.replaceAll("^\\.+", "")
                        .replaceAll("\\.+$", "")
                        .replace("\"", "\\\"");

                    result.append(cleanedLine);

                    if (i == lines.length - 1 || lines[i + 1].trim().startsWith("]")) {
                        result.append("\"\n");
                        inString = false;
                    } else {
                        result.append(" ");
                    }
                }
            }
        }

        String finalJson = result.toString();

        try {
            new ObjectMapper().readTree(finalJson);
            return finalJson;
        } catch (JsonProcessingException e) {
            throw new IOException("Failed to clean JSON: " +
                (finalJson.length() > 100 ? finalJson.substring(0, 100) + "..." : finalJson));
        }
    }
}