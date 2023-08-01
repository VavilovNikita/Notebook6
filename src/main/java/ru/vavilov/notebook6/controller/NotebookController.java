package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.repository.NotebookRepository;

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
        return "notebook/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", repository.findById(id).orElse(null));
        return "notebook/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook) {
        return "notebook/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") Notebook notebook) {
        repository.save(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", repository.findById(id).orElse(null));
        return "notebook/changePage";
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
