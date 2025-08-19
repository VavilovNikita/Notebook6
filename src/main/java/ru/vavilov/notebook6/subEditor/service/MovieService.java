package ru.vavilov.notebook6.subEditor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;
import ru.vavilov.notebook6.subEditor.model.Url;
import ru.vavilov.notebook6.subEditor.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Movie saveMovie(String name, String url, List<SubtitleEntry> subtitles) {
        Movie movie = new Movie();
        movie.setName(name);

        List<Url> urls = new ArrayList<>();
        urls.add(new Url().setUrl(url).setLanguage("ru"));
        movie.setUrls(urls);

        movie.setSubtitles(subtitles);
        return movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.getReferenceById(id);
    }

    public List<Movie> getAllMovie() {
        return movieRepository.findAll();
    }

}