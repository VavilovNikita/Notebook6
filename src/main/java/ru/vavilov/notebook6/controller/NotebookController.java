package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.NotebookRepository;
import ru.vavilov.notebook6.repository.PersonRepository;
import ru.vavilov.notebook6.service.NotebookService;

@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;

    @Autowired
    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("notebook", notebookService.findAll());
        return "notebook/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", notebookService.findNotebookById(id));
        model.addAttribute("person", notebookService.findNotebookPersonById(id));
        return "notebook/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook, Model model) {
        model.addAttribute("person", notebookService.findAllPerson());
        return "notebook/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") Notebook notebook) {
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookService.findNotebookById(id));
        model.addAttribute("person", notebookService.findAllPerson());
        return "notebook/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") Notebook notebook) {
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        notebookService.deleteNotebook(id);
        return "redirect:/notebook";
    }
}
