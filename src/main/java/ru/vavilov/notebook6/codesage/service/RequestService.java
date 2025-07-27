package ru.vavilov.notebook6.codesage.service;

import ru.vavilov.notebook6.codesage.model.RequestedData;
import ru.vavilov.notebook6.codesage.repository.RequestRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepositories requestRepositories;

    public Optional<RequestedData> findById(Long id) {
        return requestRepositories.findById(id);
    }
}
