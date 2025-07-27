package ru.vavilov.notebook6.codesage.service;

import ru.vavilov.notebook6.codesage.model.Recommendation;
import ru.vavilov.notebook6.codesage.repository.ResponseRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseService {
    private final ResponseRepositories responseRepositories;

    public List<Recommendation> findAll() {
        return responseRepositories.findAll();
    }
}
