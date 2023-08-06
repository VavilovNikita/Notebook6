package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.service.PersonService;

@Controller
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("person", personService.findAll());
        return "person/indexPage";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personService.findOne(id));
        model.addAttribute("notes", personService.findOne(id).getNotes());
        return "person/detailsPage";
    }

    @GetMapping("/new")
    public String newNotebook(@ModelAttribute("person") Person person) {
        return "person/creationPage";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") Person person) {
        personService.savePerson(person);
        return "redirect:/person";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", personService.findOne(id));
        return "person/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") Person person) {
        personService.savePerson(person);
        return "redirect:/person";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personService.deletePerson(id);
        return "redirect:/person";
    }
}
