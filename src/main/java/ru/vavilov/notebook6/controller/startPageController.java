package ru.vavilov.notebook6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class startPageController {

    @GetMapping()
    public String startPage() {
        return "redirect:/notebook";
    }
}
