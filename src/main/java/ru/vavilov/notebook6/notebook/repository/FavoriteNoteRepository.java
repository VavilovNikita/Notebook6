package ru.vavilov.notebook6.notebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.notebook.entity.FavoriteNote;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteNoteRepository extends JpaRepository<FavoriteNote, Long> {

    Optional<FavoriteNote> findByUserIdAndNotebookId(int userId, int notebookId);

    List<FavoriteNote> findAllByUserId(int userId);

    void deleteByUserIdAndNotebookId(int userId, int notebookId);
}
