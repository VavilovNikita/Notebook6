package ru.vavilov.notebook6.codesage.model;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationResponse {
    private List<Recommendation> recommendations;

    @Override
    public String toString() {
        if (recommendations == null || recommendations.isEmpty()) {
            return "Нет рекомендаций.";
        }

        StringBuilder sb = new StringBuilder();
        int index = 1;

        for (Recommendation rec : recommendations) {
            sb.append("Рекомендация ").append(index++).append(":\n");

            if (rec.getIssue() != null)
                sb.append(" - Проблема: ").append(rec.getIssue()).append("\n");

            if (rec.getDescription() != null)
                sb.append(" - Описание: ").append(rec.getDescription()).append("\n");

            if (rec.getSuggestion() != null)
                sb.append(" - Совет: ").append(rec.getSuggestion()).append("\n");

            if (rec.getExample() != null)
                sb.append(" - Пример: ").append(rec.getExample()).append("\n");

            if (rec.getNote() != null)
                sb.append(" - Заметка: ").append(rec.getNote()).append("\n");

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    public void setRequest(RequestedData messages) {
        for (Recommendation recommendation : getRecommendations()) {
            recommendation.setRequestedData(messages);
        }
    }
}
