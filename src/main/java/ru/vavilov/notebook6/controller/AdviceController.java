package ru.vavilov.notebook6.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.vavilov.notebook6.search.SearchField;
import ru.vavilov.notebook6.service.AuthService;

@ControllerAdvice
public class AdviceController {


    private final AuthService authService;

    @Autowired
    public AdviceController(AuthService authService) {
        this.authService = authService;
    }

    @ExceptionHandler
    public String exception(HttpServletResponse response, Exception exception, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        model.addAttribute("exception", exception);
        model.addAttribute("response", response);
        return "error/error";
    }
}
