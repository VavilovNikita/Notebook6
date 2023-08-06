package ru.vavilov.notebook6.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.service.PersonService;

@Component
public class PersonValidator implements Validator {
    PersonService personService;

    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (personService.findByEmail(person.getEmail()).isPresent()) {
            errors.rejectValue("email", "","Этот email уже зарегестрирован");
        }
        try {
            person.getDateOfBirth().getTime();
        }catch (Throwable e){
            errors.rejectValue("dateOfBirth", "","неверный формат даты");
        }

    }
}
