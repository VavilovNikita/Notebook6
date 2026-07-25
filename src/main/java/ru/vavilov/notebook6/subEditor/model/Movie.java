package ru.vavilov.notebook6.subEditor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Movie name cannot be empty")
    private String name;

    @Column(name = "session_id")
    private String sessionId;

    public SubtitleEntry getOneSubtitle() {
        return subtitles != null && !subtitles.isEmpty() && subtitles.get(0) != null
                ? subtitles.get(0) : new SubtitleEntry();
    }

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubtitleEntry> subtitles = new ArrayList<>();

    @Override
    public String toString() {
        String subtitlesStr = "none";
        if (subtitles != null && !subtitles.isEmpty()) {
            SubtitleEntry firstEntry = subtitles.get(0);
            subtitlesStr = String.format("SubtitleEntry[language=%s, subtitles=%s]",
                firstEntry.getLanguage(),
                firstEntry.getSubtitles().stream()
                    .map(sub -> String.format("Subtitle[id=%s, text=%s]", sub.getId(), sub.getText()))
                    .collect(Collectors.joining(", ")));
        }
        return String.format("Movie[id=%s, name=%s, subtitles=[%s]]", id, name, subtitlesStr);
    }

    public void addSubtitleEntries(List<SubtitleEntry> entries) {
        for (SubtitleEntry subtitleEntry : entries) {
            this.subtitles.add(subtitleEntry);
            subtitleEntry.setMovie(this);
        }
    }

}