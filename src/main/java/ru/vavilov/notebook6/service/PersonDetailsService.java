package ru.vavilov.notebook6.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.PersonRepository;
import ru.vavilov.notebook6.security.PersonDetails;

import java.util.Optional;

@Component
public class PersonDetailsService implements UserDetailsService {
    PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUsername(username);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("Не удается найти пользователя :" + username);
        }
        return new PersonDetails(person.get());
    }
}
