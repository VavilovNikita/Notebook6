package ru.vavilov.notebook6.wordsTranslator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.wordsTranslator.model.Word;

@Repository
public interface WordRepositories extends JpaRepository<Word, Long> {
    Word getWordByWord(String word);
}
