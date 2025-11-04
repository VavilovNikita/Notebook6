package ru.vavilov.notebook6.wordsTranslator.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class TranslateApi {
    private static final String BASE_URL =
        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ru&dt=t&q=";

    private final OkHttpClient client;

    public TranslateApi() {
        this.client = new OkHttpClient.Builder()
            .build();
    }

    public String translate(String word){
        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8);
        String url = BASE_URL + encodedWord;

        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("API error: " + response.code() + " - " + response.message());
            }

            String body = response.body().string();
            log.debug("Google Translate response: {}", body);

            return new JSONArray(body)
                .getJSONArray(0)
                .getJSONArray(0)
                .getString(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
