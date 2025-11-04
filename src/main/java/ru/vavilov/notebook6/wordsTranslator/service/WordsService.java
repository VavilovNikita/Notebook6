package ru.vavilov.notebook6.wordsTranslator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.wordsTranslator.api.TranslateApi;
import ru.vavilov.notebook6.wordsTranslator.model.Word;
import ru.vavilov.notebook6.wordsTranslator.repository.WordRepositories;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordsService {

    private final WordRepositories wordRepositories;
    private final TranslateApi translateApi;
    private final static int MAX_WORD_SIZE = 255;

    public void translate(String word) {
        if (word == null || word.trim().isEmpty() || word.length() > MAX_WORD_SIZE) {
            return;
        }
        String cleanWord = word.trim();
        try {
            Word existingWord = wordRepositories.getWordByWord(cleanWord);
            if (existingWord != null) {
                return;
            }
            String translated = translateApi.translate(cleanWord);
            if (translated == null || translated.trim().isEmpty() || translated.length() > MAX_WORD_SIZE) {
                return;
            }
            wordRepositories.save(new Word(cleanWord, translated.trim()));
        } catch (Exception ignored) {
        }
    }


    public List<Word> getAllWords() {
        return  wordRepositories.findAll();
    }

    public void deleteWordById(Long id) {
        wordRepositories.deleteById(id);
    }
}
