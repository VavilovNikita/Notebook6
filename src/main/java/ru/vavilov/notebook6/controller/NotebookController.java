package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.service.NotebookService;
import ru.vavilov.notebook6.util.NotebookValidator;

import java.util.Optional;

@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;
    private final NotebookValidator notebookValidator;

    @Autowired
    public NotebookController(NotebookService notebookService, NotebookValidator notebookValidator) {
        this.notebookService = notebookService;
        this.notebookValidator = notebookValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("notebook", notebookService.findAll());
        return "notebook/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        Notebook notebook = notebookService.findById(id);
        model.addAttribute("notebook", notebook);
        model.addAttribute("person", notebook.getPerson());
        return "notebook/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook, Model model) {
        model.addAttribute("person", notebookService.findAllPerson());
        return "notebook/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook")@Valid Notebook notebook, BindingResult bindingResult) {
        notebookValidator.validate(notebook,bindingResult);
        if (bindingResult.hasErrors()){
            return "notebook/creationPage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookService.findById(id));
        model.addAttribute("person", notebookService.findAllPerson());
        return "notebook/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook")@Valid Notebook notebook,BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
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
