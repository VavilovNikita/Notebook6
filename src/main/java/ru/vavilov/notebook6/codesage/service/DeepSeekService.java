package ru.vavilov.notebook6.codesage.service;

import ru.vavilov.notebook6.codesage.api.DeepSeekApi;
import ru.vavilov.notebook6.codesage.model.InputMode;
import ru.vavilov.notebook6.codesage.model.RecommendationResponse;
import ru.vavilov.notebook6.codesage.model.RequestedData;
import ru.vavilov.notebook6.codesage.repository.RequestRepositories;
import ru.vavilov.notebook6.codesage.repository.ResponseRepositories;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekApi deepSeekApi;
    private final ResponseRepositories responseRepositories;
    private final RequestRepositories requestRepositories;
    private final GitHubPullRequestService gitHubPullRequestService;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.stream}")
    private boolean stream;

    @Value("${deepseek.system.role}")
    private String systemRole;

    @Value("${deepseek.system.content}")
    private String systemContent;

    @Value("${deepseek.user.role}")
    private String userRole;

    @Value("${deepseek.system.example}")
    private String example;

    @Value("${deepseek.system.You-analysis-assistant}")
    private String youAnalysisAssistant;

    public String chatCompletionString(String messages, InputMode mode) {
        messages = getContent(messages, mode);
        RecommendationResponse response = deepSeekApi.chatCompletion(createRequestWithContent(messages, mode));
        response.setRequest(requestRepositories.save(new RequestedData().setRequestedData(messages)));
        responseRepositories.saveAll(response.getRecommendations());
        return response.toString();
    }

    public RecommendationResponse chatCompletion(String messages) {
        requestRepositories.save(new RequestedData().setRequestedData(messages));
        return deepSeekApi.chatCompletion(createRequestWithContent(messages, InputMode.LOGS));
    }

    public JSONObject createRequestWithContent(String userContent, InputMode mode) {
        JSONObject request = new JSONObject();
        try {
            request.put("model", model);
            request.put("stream", stream);

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", systemRole);
            systemMessage.put("content", youAnalysisAssistant + mode.getName() + systemContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", userRole);
            userMessage.put("content", userContent + " " + example);

            messages.put(systemMessage);
            messages.put(userMessage);

            request.put("messages", messages);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    private String getContent(String userContent, InputMode mode) {
        return switch (mode) {
            case LOGS, CODE -> userContent;
            case PR -> gitHubPullRequestService.getCodeFromPullRequest(userContent);
        };
    }
}
