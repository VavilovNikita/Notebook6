package ru.vavilov.notebook6.notebook.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.vavilov.notebook6.notebook.search.SearchField;
import ru.vavilov.notebook6.notebook.service.AuthService;

@ControllerAdvice
public class AdviceController {


    private final AuthService authService;

    @Autowired
    public AdviceController(AuthService authService) {
        this.authService = authService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(HttpServletResponse response, AccessDeniedException exception, Model model) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        model.addAttribute("exception", exception);
        return "error/4xx";
    }

    @ExceptionHandler
    public String exception(HttpServletResponse response, Exception exception, Model model) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        model.addAttribute("exception", exception);
        model.addAttribute("response", response);
        return "error/error";
    }
}
