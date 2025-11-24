package ru.vavilov.notebook6.notebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/cam")
public class CamController {
    @GetMapping()
    public String allUserPage() {
        return "cam/cam";
    }
}