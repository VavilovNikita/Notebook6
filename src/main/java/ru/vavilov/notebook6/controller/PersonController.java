package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.repository.PersonRepository;

@Controller
@RequestMapping("/person")
public class PersonController {
    private final PersonRepository repository;

    @Autowired
    public PersonController(PersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("person", repository.findAll());
        return "person/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", repository.findById(id).orElse(null));
        model.addAttribute("notes", repository.findById(id).orElse(null).getNotes());
        return "person/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("person") Person person) {
        return "person/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") Person person) {
        repository.save(person);
        return "redirect:/person";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", repository.findById(id).orElse(null));
        return "person/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") Person person) {
        repository.save(person);
        return "redirect:/person";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        repository.deleteById(id);
        return "redirect:/person";
    }
}
