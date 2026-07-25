package ru.vavilov.notebook6.codesage.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Shared builder for DeepSeek chat-completion request bodies, used by both
 * CodeSage (code review) and SubEditor (subtitle translation) so the two
 * modules don't each duplicate the same request JSON assembly.
 */
public final class DeepSeekRequestFactory {

    private DeepSeekRequestFactory() {
    }

    public static JSONObject buildRequest(String model, boolean stream,
                                           String systemRole, String systemContent,
                                           String userRole, String userContent,
                                           Integer maxTokens) {
        JSONObject request = new JSONObject();
        try {
            request.put("model", model);
            request.put("stream", stream);
            if (maxTokens != null) {
                request.put("max_tokens", maxTokens);
            }

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", systemRole);
            systemMessage.put("content", systemContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", userRole);
            userMessage.put("content", userContent);

            messages.put(systemMessage);
            messages.put(userMessage);

            request.put("messages", messages);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return request;
    }
}
