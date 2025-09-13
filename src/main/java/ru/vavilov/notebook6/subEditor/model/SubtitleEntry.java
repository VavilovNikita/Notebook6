package ru.vavilov.notebook6.subEditor.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SubtitleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "subtitle_entry_id")
    @jakarta.persistence.OrderBy("startMillis ASC")
    private List<Subtitle> subtitles = new ArrayList<>();

    @NotNull(message = "Language cannot be null")
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public List<String> getListWithText() {
        return subtitles.stream()
            .map(Subtitle::getText)
            .toList();
    }

    public SubtitleEntry setSubtitlesByTranslatedArray(List<String> translatedTexts) {
        if (translatedTexts == null) {
            throw new IllegalArgumentException("Translated texts cannot be null");
        }

        SubtitleEntry translatedEntry = new SubtitleEntry()
            .setSubtitles(new ArrayList<>());

        if (translatedTexts.size() != this.subtitles.size()) {
            System.err.println("WARNING: Subtitle size mismatch. Using original text for missing translations.");
            System.err.println("Original: " + this.subtitles.size() + ", Translated: " + translatedTexts.size());

            List<String> fixedTexts = new ArrayList<>();
            for (int i = 0; i < this.subtitles.size(); i++) {
                if (i < translatedTexts.size() && translatedTexts.get(i) != null) {
                    fixedTexts.add(translatedTexts.get(i));
                } else {
                    // Используем оригинальный текст для пропущенных переводов
                    fixedTexts.add(this.subtitles.get(i).getText());
                    System.err.println("Using original text for subtitle #" + i);
                }
            }
            translatedTexts = fixedTexts;
        }

        for (int i = 0; i < this.subtitles.size(); i++) {
            Subtitle originalSubtitle = this.subtitles.get(i);
            String translatedText = translatedTexts.get(i);

            Subtitle translatedSubtitle = new Subtitle()
                .setStartMillis(originalSubtitle.getStartMillis())
                .setEndMillis(originalSubtitle.getEndMillis())
                .setText(translatedText)
                .setSubtitleEntry(translatedEntry);

            translatedEntry.getSubtitles().add(translatedSubtitle);
        }

        return translatedEntry;
    }
}