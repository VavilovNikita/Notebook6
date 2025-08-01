package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class SearchService {
    private final NotebookRepository notebookRepository;

    @Autowired
    public SearchService(NotebookRepository notebookRepository) {
        this.notebookRepository = notebookRepository;
    }

    @Transactional
    public Set<Notebook> getNotesByTitleOrText(String s) {
        Set<Notebook> notebookSet = new HashSet<>();
        notebookSet.addAll(notebookRepository.findAllByTitleContainingIgnoreCase(s));
        notebookSet.addAll(notebookRepository.findAllByTextContainingIgnoreCase(s));
        return notebookSet;
    }
}
