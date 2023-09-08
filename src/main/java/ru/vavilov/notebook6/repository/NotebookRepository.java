package ru.vavilov.notebook6.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.entity.Notebook;

import java.util.Optional;

@Repository
public interface NotebookRepository extends CrudRepository<Notebook, Integer> {
    Iterable<Notebook> findAllByOrderByPositionDesc();
}
