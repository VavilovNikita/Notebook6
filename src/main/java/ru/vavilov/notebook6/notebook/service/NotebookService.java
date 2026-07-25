package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final AuthService authService;

    @Autowired
    public NotebookService(NotebookRepository notebookRepository, AuthService authService) {
        this.notebookRepository = notebookRepository;
        this.authService = authService;
    }

    public Iterable<Notebook> findAll(PageRequest pageRequest) {
        return notebookRepository.findAll(pageRequest);
    }


    public Notebook findById(int id) {
        return notebookRepository.findById(id).orElse(null);
    }


    public void saveNotebook(Notebook notebook) {
        User currentUser = authService.getUser();
        Optional<Notebook> existing = notebookRepository.findById(notebook.getId());
        if (existing.isPresent()) {
            Notebook existingNotebook = existing.get();
            if (!isOwner(existingNotebook, currentUser)) {
                throw new AccessDeniedException("Нет прав на изменение этой заметки");
            }
            notebook.setUser(existingNotebook.getUser());
            notebook.setCreatedAt(existingNotebook.getCreatedAt());
        } else {
            notebook.setUser(currentUser);
            notebook.setCreatedAt(LocalDate.now());
        }
        notebook.setUpdatedAt(LocalDate.now());
        notebookRepository.save(notebook);
    }

    public void deleteNotebook(int id) {
        User currentUser = authService.getUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("Заметка не найдена"));
        if (!isOwner(notebook, currentUser)) {
            throw new AccessDeniedException("Нет прав на удаление этой заметки");
        }
        notebookRepository.deleteById(id);
    }

    private boolean isOwner(Notebook notebook, User user) {
        return notebook.getUser() != null && notebook.getUser().getId() == user.getId();
    }
}
