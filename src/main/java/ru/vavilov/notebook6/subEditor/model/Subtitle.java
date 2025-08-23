package ru.vavilov.notebook6.subEditor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Subtitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_time_millis")
    private Long startMillis;

    @NotNull
    @Column(name = "end_time_millis")
    private Long endMillis;

    @NotBlank(message = "Subtitle text cannot be empty")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtitle_entry_id", nullable = false)
    private SubtitleEntry subtitleEntry;

    // Вспомогательные методы для удобства
    public String getStartFormatted() {
        return formatMillisToTime(startMillis);
    }

    public String getEndFormatted() {
        return formatMillisToTime(endMillis);
    }

    private String formatMillisToTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long remainingMillis = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, remainingMillis);
    }
}