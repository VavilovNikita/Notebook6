package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
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

    private static final int batchSize = 120;



    public SubtitleEntry chatCompletionString(SubtitleEntry subtitleEntry, Language language) {
        return deepSeekApi.chatCompletionTranslator(createRequestsWithContent(subtitleEntry, language), subtitleEntry);
    }

    public List<JSONObject> createRequestsWithContent(SubtitleEntry subtitleEntry, Language language) {
        List<JSONObject> requests = new ArrayList<>();
        List<String> textLines = subtitleEntry.getListWithText();

        for (int i = 0; i < textLines.size(); i += batchSize) {
            int fromIndex = i;
            int toIndex = Math.min(i + batchSize, textLines.size());

            List<String> batchLines = textLines.subList(fromIndex, toIndex);

            String batchContent = String.join("\n", batchLines);

            try {
                JSONObject request = new JSONObject();
                request.put("model", model);
                request.put("stream", stream);

                JSONArray messages = new JSONArray();

                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", systemRole);
                systemMessage.put("content", you + " " + language.getNameNative() + " " + systemContent);

                JSONObject userMessage = new JSONObject();
                userMessage.put("role", userRole);
                userMessage.put("content", "Translate from "
                    + subtitleEntry.getLanguage() + " to "
                    + language.getNameNative() + ". Translate only the following lines "
                    + (fromIndex + 1) + " to " + toIndex + ":\n"
                    + batchContent);

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
