package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.codesage.api.DeepSeekApi;
import ru.vavilov.notebook6.subEditor.model.Movie;

import java.util.Locale;
@Service("subEditorDeepSeekService")
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekApi deepSeekApi;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.stream}")
    private boolean stream;

    @Value("${deepseek.system.role}")
    private String systemRole;

    @Value("${subEditor.deepseek.api.system-content}")
    private String systemContent;

    @Value("${deepseek.user.role}")
    private String userRole;

    @Value("${subEditor.deepseek.api.example}")
    private String example;

    @Value("${subEditor.deepseek.api.you-analysis-assistant}")
    private String you;



    public Movie chatCompletionString(Movie movie) {
        return deepSeekApi.chatCompletionTranslator(createRequestWithContent(movie, null));
    }

    public JSONObject createRequestWithContent(Movie movie, Locale locale) {
        JSONObject request = new JSONObject();
        try {
            request.put("model", model);
            request.put("stream", stream);

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", systemRole);
            systemMessage.put("content", you + " " + getLocale(locale) + " " + systemContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", userRole);
            userMessage.put("content", movie.toString() + " " + example);

            messages.put(systemMessage);
            messages.put(userMessage);

            request.put("messages", messages);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to create JSON request", e);
        }
        return request;
    }

    private String getLocale(Locale locale) {
        return locale != null ? locale.getDisplayName() : "";
    }
}
