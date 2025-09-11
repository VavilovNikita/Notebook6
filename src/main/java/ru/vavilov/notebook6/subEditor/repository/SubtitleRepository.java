package ru.vavilov.notebook6.subEditor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

public interface SubtitleRepository extends JpaRepository<SubtitleEntry, Long> {
}