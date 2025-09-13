package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.codesage.api.DeepSeekApi;
import ru.vavilov.notebook6.subEditor.model.Language;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
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

    @Value("${subEditor.deepseek.api.you-analysis-assistant}")
    private String you;

    private static final int batchSize = 150;


    public SubtitleEntry chatCompletionString(SubtitleEntry subtitleEntry, Language language) {
        return deepSeekApi.chatCompletionTranslator(createRequestsWithContent(subtitleEntry, language), subtitleEntry);
    }

    public List<JSONObject> createRequestsWithContent(SubtitleEntry subtitleEntry, Language language) {
        List<JSONObject> requests = new ArrayList<>();
        List<String> textLines = subtitleEntry.getListWithText();
        log.info("lines size is " + textLines.size());

        for (int i = 0; i < textLines.size(); i += batchSize) {
            int fromIndex = i;
            int toIndex = Math.min(i + batchSize, textLines.size());

            List<String> batchLines = textLines.subList(fromIndex, toIndex);

            try {
                JSONObject request = new JSONObject();
                request.put("model", model);
                request.put("stream", stream);
                request.put("max_tokens", 8000);

                JSONArray messages = new JSONArray();

                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", systemRole);
                systemMessage.put("content", "Translate Russian to " + language.getNameNative() +
                    ". Input is JSON array. Output MUST be JSON array with SAME number of elements. Do not modify structure.");

                JSONObject userMessage = new JSONObject();
                userMessage.put("role", userRole);

                JSONArray inputArray = new JSONArray(batchLines);
                String userContent = String.format(
                    "Translate from %s to %s. Input JSON array has %d elements. Output MUST be JSON array with EXACTLY %d elements:\n%s",
                    subtitleEntry.getLanguage(),
                    language.getNameNative(),
                    batchLines.size(),
                    batchLines.size(),
                    inputArray.toString()
                );

                userMessage.put("content", userContent);

                messages.put(systemMessage);
                messages.put(userMessage);

                request.put("messages", messages);
                requests.add(request);

            } catch (JSONException e) {
                throw new RuntimeException("Failed to create JSON request for batch " + fromIndex + "-" + toIndex, e);
            }
        }
        return requests;
    }
}
