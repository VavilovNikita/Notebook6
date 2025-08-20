package ru.vavilov.notebook6.subEditor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vavilov.notebook6.subEditor.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie getMovieByName(String name);
}