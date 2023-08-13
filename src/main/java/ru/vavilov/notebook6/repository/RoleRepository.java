package ru.vavilov.notebook6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
