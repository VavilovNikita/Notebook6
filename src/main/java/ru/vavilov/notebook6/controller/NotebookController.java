package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.repository.NotebookDAO;
import ru.vavilov.notebook6.repository.NotebookRepository;

import java.util.Collections;

@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookRepository repository;

    @Autowired
    public NotebookController(NotebookRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("notebook", repository.findAll());
        return "notebook/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", repository.findById(id));
        return "notebook/show";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook) {
        return "notebook/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") Notebook notebook) {
        repository.save(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", repository.findById(id));
        return "notebook/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") Notebook notebook) {
        repository.save(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        repository.deleteById(id);
        return "redirect:/notebook";
    }
}
