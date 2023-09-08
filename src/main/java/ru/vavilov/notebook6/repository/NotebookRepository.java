package ru.vavilov.notebook6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.entity.Notebook;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Iterable<Notebook> findAllByOrderByPositionDesc();
}
