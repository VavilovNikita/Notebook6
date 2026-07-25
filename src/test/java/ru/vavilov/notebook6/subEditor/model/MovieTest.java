package ru.vavilov.notebook6.subEditor.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieTest {

    @Test
    void getOneSubtitle_returnsEmptyEntry_whenSubtitlesIsNull() {
        Movie movie = new Movie().setSubtitles(null);

        SubtitleEntry result = movie.getOneSubtitle();

        assertThat(result).isNotNull();
        assertThat(result.getSubtitles()).isNullOrEmpty();
    }

    @Test
    void getOneSubtitle_returnsEmptyEntry_whenSubtitlesIsEmpty() {
        Movie movie = new Movie().setSubtitles(Collections.emptyList());

        SubtitleEntry result = movie.getOneSubtitle();

        assertThat(result).isNotNull();
    }

    @Test
    void getOneSubtitle_returnsFirstEntry_whenSubtitlesPresent() {
        SubtitleEntry entry = new SubtitleEntry().setLanguage("Русский");
        List<SubtitleEntry> subtitles = List.of(entry);
        Movie movie = new Movie().setSubtitles(subtitles);

        SubtitleEntry result = movie.getOneSubtitle();

        assertThat(result).isSameAs(entry);
    }
}
