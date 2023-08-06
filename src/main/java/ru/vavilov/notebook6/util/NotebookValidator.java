package ru.vavilov.notebook6.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.service.NotebookService;
import ru.vavilov.notebook6.service.PersonService;

@Component
public class NotebookValidator implements Validator {
    NotebookService notebookService;

    @Autowired
    public NotebookValidator(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Notebook.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Notebook notebook = (Notebook) target;
        if (notebookService.findByText(notebook.getText()).isPresent()) {
            errors.rejectValue("text", "","Запись с таким текстом уже существует");
        }
        if (notebookService.findByTitle(notebook.getTitle()).isPresent()) {
            errors.rejectValue("title", "","Запись с таким заголовком уже существует");
        }
    }
}
