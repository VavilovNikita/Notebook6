package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.repository.NotebookRepository;

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

    public Iterable<Notebook> findAll() {
        return notebookRepository.findAllByOrderByPositionDesc();
    }


    public Notebook findById(int id) {
        return notebookRepository.findById(id).orElse(null);
    }


    public void saveNotebook(Notebook notebook) {
        notebook.setUser(authService.getUser());
        notebook.setUpdatedAt(LocalDate.now());
        if(notebookRepository.findById(notebook.getId()).isEmpty()){
            notebook.setCreatedAt(LocalDate.now());
        }else {
            notebook.setCreatedAt(notebookRepository.findById(notebook.getId()).get().getCreatedAt());
        }
        notebookRepository.save(notebook);
    }

    public void deleteNotebook(int id) {
        notebookRepository.deleteById(id);
    }

    public Optional<Notebook> findByText(String text) {
        return notebookRepository.findByText(text);
    }

    public Optional<Notebook> findByTitle(String title) {
        return notebookRepository.findByTitle(title);
    }


}
