package ru.vavilov.notebook6.wordsTranslator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.vavilov.notebook6.wordsTranslator.service.TranslationException;
import ru.vavilov.notebook6.wordsTranslator.service.WordsService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/translator")
public class TranslateController {

    private final WordsService wordsService;

    @GetMapping("/words")
    public String showAllWords(Model model) {
        model.addAttribute("words", wordsService.getAllWords());
        return "wordsTranslator/words";
    }

    @PostMapping("/translate")
    @ResponseBody
    public ResponseEntity<String> translateWord(@RequestParam String word) {
        try {
            wordsService.translate(word);
            return ResponseEntity.ok("Слово переведено");
        } catch (TranslationException e) {
            log.warn("Перевод слова не выполнен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @GetMapping("")
    public String handleExport() {
        return "wordsTranslator/translate";
    }

    @PostMapping("/delete/{id}")
    public String deleteWord(@PathVariable Long id) {
        wordsService.deleteWordById(id);
        return "wordsTranslator/words";
    }
}
