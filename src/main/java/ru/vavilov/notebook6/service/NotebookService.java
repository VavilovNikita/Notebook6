package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.NotebookRepository;
import ru.vavilov.notebook6.repository.PersonRepository;

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

    public Person findNotebookPersonById(int id) {
        return notebookRepository.findById(id).orElse(null).getPerson();
    }

    public Notebook findNotebookById(int id) {
        return notebookRepository.findById(id).orElse(null);
    }

    public Iterable<Person> findAllPerson() {
        return personRepository.findAll();
    }

    public void saveNotebook(Notebook notebook) {
        notebookRepository.save(notebook);
    }

    public void deleteNotebook(int id) {
        notebookRepository.deleteById(id);
    }
}
