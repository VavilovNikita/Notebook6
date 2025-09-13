package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.subEditor.model.Language;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubType;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;
import ru.vavilov.notebook6.subEditor.repository.MovieRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final DeepSeekService deepSeekService;

    public Movie parseSubtitles(MultipartFile file) throws IOException {

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Filename is required");

        byte[] fileBytes = file.getBytes();
        List<SubtitleEntry> subtitles = null;
        switch (SubType.getTypeFromName(originalFilename)) {
            case ASS -> {
                try (InputStream parseStream = new ByteArrayInputStream(fileBytes)) {
                    subtitles = SubParser.parseASS(parseStream, "Русский");
                }
            }
            case SRT -> {
                try (InputStream parseStream = new ByteArrayInputStream(fileBytes)) {
                    subtitles = SubParser.parseSRT(parseStream, "Русский");
                }
            }
            case SSA -> {

            }
            case VTT -> {

            }
            default -> throw new IOException("Unknown file type");
        }
        Movie existMovie = movieRepository.getMovieByName(originalFilename);
        if (existMovie != null) {
            return existMovie;
        } else {
            Movie movie = new Movie()
                .setName(originalFilename);

            movie.addSubtitleEntries(subtitles);

            return movieRepository.save(movie);
        }
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Movie with id " + id + " not found")
        );
    }

    public List<Movie> getAllMovie() {
        return movieRepository.findAll();
    }

    public Movie translateAndSaveMovie(Long movieId, Long languageId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isPresent()) {
            Language language = Language.getById(languageId);
            Movie transletedMovie = new Movie().setSubtitles(
                Collections.singletonList(deepSeekService.chatCompletionString(
                    movie.get().getOneSubtitle(), language)
                )
            );
            SubtitleEntry subtitleEntry = transletedMovie.getOneSubtitle();
            subtitleEntry.setMovie(movie.get());
            subtitleEntry.setLanguage(language.getNameNative());
            movie.get().getSubtitles().add(subtitleEntry);
            movieRepository.save(movie.get());
            return transletedMovie;
        }
        return new Movie();
    }
}
