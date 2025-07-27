package ru.vavilov.notebook6.codesage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.codesage.model.RecommendationResponse;
import ru.vavilov.notebook6.codesage.repository.ResponseRepositories;

@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final DeepSeekService deepSeekService;
    private final ResponseRepositories responseRepositories;

    @KafkaListener(topics = "logs", groupId = "log-group")
    public void consume(String logMessage) {
        RecommendationResponse response = deepSeekService.chatCompletion(logMessage);
        responseRepositories.saveAll(response.getRecommendations());
    }
}

