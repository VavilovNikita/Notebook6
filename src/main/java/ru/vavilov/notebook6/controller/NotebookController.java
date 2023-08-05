package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.NotebookRepository;
import ru.vavilov.notebook6.repository.PersonRepository;

@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookRepository notebookRepository;
    private final PersonRepository personRepository;

    @Autowired
    public NotebookController(NotebookRepository repository, PersonRepository personRepository, PersonRepository personRepository1) {
        this.notebookRepository = repository;
        this.personRepository = personRepository1;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("notebook", notebookRepository.findAll());
        return "notebook/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", notebookRepository.findById(id).orElse(null));
        model.addAttribute("person", notebookRepository.findById(id).orElse(null).getPerson());
        return "notebook/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("notebook") Notebook notebook,Model model) {
        model.addAttribute("person",personRepository.findAll());
        return "notebook/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") Notebook notebook) {
        notebookRepository.save(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookRepository.findById(id).orElse(null));
        return "notebook/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") Notebook notebook) {
        notebookRepository.save(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        notebookRepository.deleteById(id);
        return "redirect:/notebook";
    }
}
