package ru.vavilov.notebook6.wordsTranslator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.codesage.service.GitExportService;
import ru.vavilov.notebook6.wordsTranslator.service.WordsService;

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
    public String showExportForm(@RequestParam String word) {
        wordsService.translate(word);
        return "wordsTranslator/translate";
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
