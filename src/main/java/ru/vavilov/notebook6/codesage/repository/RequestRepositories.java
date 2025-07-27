package ru.vavilov.notebook6.codesage.repository;

import ru.vavilov.notebook6.codesage.model.RequestedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepositories extends JpaRepository<RequestedData, Long> {
}
