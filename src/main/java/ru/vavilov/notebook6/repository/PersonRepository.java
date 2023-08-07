package ru.vavilov.notebook6.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person,Integer> {

    Optional<Person> findByEmail(String email);
}
