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

    public SubtitleEntry setSubtitlesByTranslatedArray(List<String> texts) {
        SubtitleEntry subtitleEntry;
        try {
            subtitleEntry = (SubtitleEntry) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (texts == null || texts.size() != this.subtitles.size()) {
            throw new IllegalArgumentException("Subtitle size mismatch");
        }
        for (int i = 0; i < texts.size(); i++) {
            subtitleEntry.getSubtitles().get(i).setText(texts.get(i));
        }
        return subtitleEntry;
    }
}