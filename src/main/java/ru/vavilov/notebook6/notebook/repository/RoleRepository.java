package ru.vavilov.notebook6.notebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.notebook.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
