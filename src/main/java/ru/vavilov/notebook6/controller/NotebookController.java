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
    public NotebookController(NotebookService notebookService, NotebookValidator notebookValidator,AuthService authService) {
        this.notebookService = notebookService;
        this.notebookValidator = notebookValidator;
        this.authService = authService;
    }

    @GetMapping("/allNotes")
    public String index(Model model) {
        model.addAttribute("notebook", notebookService.findAll());
        return "notebook/indexPage";
    }

    @GetMapping()
    public String indexById(Model model) {
        model.addAttribute("notebook", authService.getUser().getNotes());
        return "notebook/indexByIdPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        Notebook notebook = notebookService.findById(id);
        model.addAttribute("notebook", notebook);
        model.addAttribute("user", notebook.getUser());
        return "notebook/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook) {
        return "notebook/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult) {
        notebookValidator.validate(notebook, bindingResult);
        if (bindingResult.hasErrors()) {
            return "notebook/creationPage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookService.findById(id));
        model.addAttribute("user", notebookService.findAllUser());
        return "notebook/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notebook/changePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        notebookService.deleteNotebook(id);
        return "redirect:/notebook";
    }
}
