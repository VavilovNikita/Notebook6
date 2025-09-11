package ru.vavilov.notebook6.subEditor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubType;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;
import ru.vavilov.notebook6.subEditor.repository.MovieRepository;
import ru.vavilov.notebook6.subEditor.repository.SubtitleRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubtitleService {

    private final SubtitleRepository subtitleRepository;

    public SubtitleEntry getSubtitleById(Long id) {
        return subtitleRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Subtitle with id " + id + " not found")
        );
    }
}
