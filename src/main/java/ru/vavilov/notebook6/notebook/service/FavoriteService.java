package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.notebook.entity.FavoriteNote;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.entity.Visibility;
import ru.vavilov.notebook6.notebook.repository.FavoriteNoteRepository;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteNoteRepository favoriteNoteRepository;
    private final NotebookRepository notebookRepository;
    private final AuthService authService;

    @Autowired
    public FavoriteService(FavoriteNoteRepository favoriteNoteRepository, NotebookRepository notebookRepository,
                            AuthService authService) {
        this.favoriteNoteRepository = favoriteNoteRepository;
        this.notebookRepository = notebookRepository;
        this.authService = authService;
    }

    /**
     * Toggles the favorite flag for the current user and returns the new state
     * (true = now favorited). Only notes the user can actually see (their own,
     * or TEAM-visibility notes) can be favorited.
     */
    public boolean toggleFavorite(int notebookId) {
        User currentUser = authService.getUser();
        Notebook notebook = notebookRepository.findById(notebookId)
                .orElseThrow(() -> new IllegalArgumentException("Заметка не найдена: " + notebookId));
        boolean isOwner = notebook.getUser() != null && notebook.getUser().getId() == currentUser.getId();
        if (!isOwner && notebook.getVisibility() != Visibility.TEAM) {
            throw new AccessDeniedException("Нельзя добавить в избранное недоступную заметку");
        }

        return favoriteNoteRepository.findByUserIdAndNotebookId(currentUser.getId(), notebookId)
                .map(existing -> {
                    favoriteNoteRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    favoriteNoteRepository.save(new FavoriteNote(currentUser, notebook));
                    return true;
                });
    }

    public List<Notebook> getFavoriteNotes() {
        User currentUser = authService.getUser();
        return favoriteNoteRepository.findAllByUserId(currentUser.getId()).stream()
                .map(FavoriteNote::getNotebook)
                .collect(Collectors.toList());
    }

    public Set<Integer> getFavoriteNotebookIds() {
        User currentUser = authService.getUser();
        return favoriteNoteRepository.findAllByUserId(currentUser.getId()).stream()
                .map(favorite -> favorite.getNotebook().getId())
                .collect(Collectors.toSet());
    }
}
