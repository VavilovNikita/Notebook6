package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.codesage.api.DeepSeekApi;
import ru.vavilov.notebook6.codesage.api.DeepSeekRequestFactory;
import ru.vavilov.notebook6.subEditor.model.Language;
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

    @Value("${deepseek.user.role}")
    private String userRole;

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
                String systemContent = "Translate Russian to " + language.getNameNative() +
                    ". You will receive a JSON array containing " + batchLines.size() + " strings.\n\n" +
                    "CRITICAL JSON ESCAPING RULES:\n" +
                    "1. Preserve original \\n, \\t, \\\", \\\\ exactly as is\n" +
                    "2. ONLY these escape sequences are valid in JSON: \\\", \\\\, \\/, \\b, \\f, \\n, \\r, \\t\n" +
                    "3. NEVER use \\a, \\x, \\u (unless it's a valid \\uXXXX unicode sequence)\n" +
                    "4. Escape quotes in translated text: \" becomes \\\"\n" +
                    "5. If you need to use backslash, escape it: \\ becomes \\\\\n\n" +
                    "STRICT REQUIREMENTS:\n" +
                    "• Output must be valid JSON array with exactly " + batchLines.size() + " elements\n" +
                    "• Maintain original element order\n" +
                    "• All escape sequences must be valid JSON escapes\n" +
                    "• The output must pass JSON.parse() validation\n\n" +
                    "INVALID EXAMPLE: \"text\\a\" (\\a is not a valid JSON escape)\n" +
                    "VALID EXAMPLE: \"text with\\nnewline and \\\"quotes\\\"\"";

                JSONArray inputArray = new JSONArray(batchLines);
                String userContent = String.format(
                    "Translate from %s to %s. Input JSON array has %d elements. Output MUST be JSON array with EXACTLY %d elements:\n%s",
                    subtitleEntry.getLanguage(),
                    language.getNameNative(),
                    batchLines.size(),
                    batchLines.size(),
                    inputArray.toString()
                );

                JSONObject request = DeepSeekRequestFactory.buildRequest(
                    model, stream, systemRole, systemContent, userRole, userContent, 8000);
                requests.add(request);

            } catch (JSONException e) {
                throw new RuntimeException("Failed to create JSON request for batch " + fromIndex + "-" + toIndex, e);
            }
        }
        return requests;
    }
}
