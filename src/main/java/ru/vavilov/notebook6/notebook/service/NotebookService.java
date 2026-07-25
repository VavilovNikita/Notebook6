package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.entity.Visibility;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final AuthService authService;
    private final TagService tagService;

    @Autowired
    public NotebookService(NotebookRepository notebookRepository, AuthService authService, TagService tagService) {
        this.notebookRepository = notebookRepository;
        this.authService = authService;
        this.tagService = tagService;
    }

    public Iterable<Notebook> findAll(PageRequest pageRequest) {
        return notebookRepository.findAll(pageRequest);
    }


    public Notebook findById(int id) {
        return notebookRepository.findById(id).orElse(null);
    }

    public List<Notebook> getMyNotes(String tagFilter) {
        return filterByTag(authService.getUser().getNotes(), tagFilter);
    }

    public List<Notebook> getTeamLibrary(String tagFilter) {
        return filterByTag(notebookRepository.findAllByVisibilityOrderByPositionDesc(Visibility.TEAM), tagFilter);
    }

    private List<Notebook> filterByTag(List<Notebook> notes, String tagFilter) {
        if (tagFilter == null || tagFilter.isBlank()) {
            return notes;
        }
        return notes.stream()
                .filter(note -> note.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(tagFilter)))
                .collect(Collectors.toList());
    }

    public void saveNotebook(Notebook notebook, String tagNamesCsv) {
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
        notebook.setVisibility(notebook.getVisibility() == null ? Visibility.PERSONAL : notebook.getVisibility());
        notebook.setTags(tagService.resolveOrCreate(tagNamesCsv));
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
