package ru.vavilov.notebook6.subEditor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vavilov.notebook6.subEditor.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie getMovieByNameAndSessionId(String name, String sessionId);
    List<Movie> findAllBySessionId(String sessionId);
}