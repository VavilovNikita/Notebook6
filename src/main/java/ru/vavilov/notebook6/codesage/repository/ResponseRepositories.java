package ru.vavilov.notebook6.codesage.repository;

import ru.vavilov.notebook6.codesage.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepositories extends JpaRepository<Recommendation, Long> {
}
