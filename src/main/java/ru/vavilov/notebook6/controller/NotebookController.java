package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.service.AuthService;
import ru.vavilov.notebook6.service.NotebookService;
import ru.vavilov.notebook6.util.NotebookValidator;


@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;
    private final NotebookValidator notebookValidator;
    private final AuthService authService;

    @Autowired
    public NotebookController(NotebookService notebookService, NotebookValidator notebookValidator, AuthService authService) {
        this.notebookService = notebookService;
        this.notebookValidator = notebookValidator;
        this.authService = authService;
    }

    @GetMapping("/allNotes")
    public String getNotes(Model model) {
        model.addAttribute("notebook", notebookService.findAll());
        return "notebook/allNotesPage";
    }

    @GetMapping()
    public String getUserNotes(Model model) {
        model.addAttribute("notebook", authService.getUser().getNotes());
        return "notebook/notesByUserIdPage";
    }

    @GetMapping("/{id}")
    public String noteInfo(@PathVariable("id") int id, Model model) {
        Notebook notebook = notebookService.findById(id);
        model.addAttribute("notebook", notebook);
        model.addAttribute("user", notebook.getUser());
        return "notebook/noteInfoPage";
    }

    @GetMapping("/new")
    public String newNote(@ModelAttribute("notebook") Notebook notebook) {
        return "notebook/createNotePage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult) {
        notebookValidator.validate(notebook, bindingResult);
        if (bindingResult.hasErrors()) {
            return "notebook/createNotePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String editNote(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookService.findById(id));
        return "notebook/changeNotePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notebook/changeNotePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable("id") int id) {
        notebookService.deleteNotebook(id);
        return "redirect:/notebook";
    }
}
