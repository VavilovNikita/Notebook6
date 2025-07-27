package ru.vavilov.notebook6.codesage.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.vavilov.notebook6.codesage.model.InputMode;
import ru.vavilov.notebook6.codesage.model.Recommendation;
import ru.vavilov.notebook6.codesage.model.RequestedData;
import ru.vavilov.notebook6.codesage.service.DeepSeekService;
import ru.vavilov.notebook6.codesage.service.RequestService;
import ru.vavilov.notebook6.codesage.service.ResponseService;

import java.util.List;

@Controller
@RequiredArgsConstructor
class ReviewController {
    private final DeepSeekService deepSeekService;
    private final ResponseService responseService;
    private final RequestService requestService;

    @GetMapping("/codesaga/")
    public String showForm() {
        return "codesaga/reviewForm";
    }

    @PostMapping("/codesaga/submitReview")
    public String submitReview(
        @RequestParam("mode") InputMode mode,
        @RequestParam(value = "inputText", required = false) String inputText, Model model) {

        String response = deepSeekService.chatCompletionString(inputText, mode);
        model.addAttribute("message", response);
        return "codesaga/reviewForm";
    }

    @GetMapping("/codesaga/reviewHistory")
    public String getReviewHistory(Model model) {
        List<Recommendation> recommendations = responseService.findAll();
        model.addAttribute("recommendations", recommendations);
        return "codesaga/reviewHistory";
    }

    @GetMapping("/codesaga/review/{id}")
    public String viewRequestDetails(@PathVariable Long id, Model model) {
        RequestedData data = requestService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        model.addAttribute("requestedData", data);
        return "codesaga/reviewDetails";
    }

}
