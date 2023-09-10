package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.repository.NotebookRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class SearchService {
    private final NotebookRepository notebookRepository;

    @Autowired
    public SearchService(NotebookRepository notebookRepository) {
        this.notebookRepository = notebookRepository;
    }

    public Set<Notebook> getNotesByTitleOrText(String s){
        Set<Notebook> notebookSet = new HashSet<>();
        notebookSet.addAll(notebookRepository.findAllByTitleContainingIgnoreCase(s));
        notebookSet.addAll(notebookRepository.findAllByTextContainingIgnoreCase(s));
        return notebookSet;
    }
}
