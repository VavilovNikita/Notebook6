package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.NotebookRepository;
import ru.vavilov.notebook6.repository.PersonRepository;
import ru.vavilov.notebook6.security.PersonDetails;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final PersonRepository personRepository;

    @Autowired
    public NotebookService(NotebookRepository notebookRepository, PersonRepository personRepository) {
        this.notebookRepository = notebookRepository;
        this.personRepository = personRepository;
    }

    public Iterable<Notebook> findAll() {
        return notebookRepository.findAll();
    }


    public Notebook findById(int id) {
        return notebookRepository.findById(id).orElse(null);
    }

    public Iterable<Person> findAllPerson() {
        return personRepository.findAll();
    }

    public void saveNotebook(Notebook notebook) {
        notebook.setPerson(getAuthPerson());
        notebook.setCreatedAt(new Date());
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

    public Person getAuthPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personRepository.findById(personDetails.getPerson().getId()).orElse(null);
    }
}
