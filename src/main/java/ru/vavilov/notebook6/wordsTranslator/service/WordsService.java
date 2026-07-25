package ru.vavilov.notebook6.wordsTranslator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.wordsTranslator.api.TranslateApi;
import ru.vavilov.notebook6.wordsTranslator.model.Word;
import ru.vavilov.notebook6.wordsTranslator.repository.WordRepositories;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordsService {

    private final WordRepositories wordRepositories;
    private final TranslateApi translateApi;
    private final static int MAX_WORD_SIZE = 255;

    public void translate(String word) {
        if (word == null || word.trim().isEmpty() || word.length() > MAX_WORD_SIZE) {
            throw new TranslationException("Слово пустое или слишком длинное");
        }
        String cleanWord = word.trim();
        try {
            Word existingWord = wordRepositories.getWordByWord(cleanWord);
            if (existingWord != null) {
                return;
            }
            String translated = translateApi.translate(cleanWord);
            if (translated == null || translated.trim().isEmpty() || translated.length() > MAX_WORD_SIZE) {
                throw new TranslationException("Сервис перевода вернул пустой результат для слова: " + cleanWord);
            }
            wordRepositories.save(new Word(cleanWord, translated.trim()));
        } catch (TranslationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Не удалось перевести слово '{}'", cleanWord, e);
            throw new TranslationException("Не удалось перевести слово: " + cleanWord, e);
        }
    }


    public List<Word> getAllWords() {
        return  wordRepositories.findAll();
    }

    public void deleteWordById(Long id) {
        wordRepositories.deleteById(id);
    }
}
