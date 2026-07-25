package ru.vavilov.notebook6.notebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.Visibility;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {

    List<Notebook> findAllByTitleContainingIgnoreCase(String s);
    List<Notebook> findAllByTextContainingIgnoreCase(String s);
    List<Notebook> findAllByVisibilityOrderByPositionDesc(Visibility visibility);
}
