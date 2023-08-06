package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.service.PersonService;
import ru.vavilov.notebook6.util.PersonValidator;

@Controller
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final PersonValidator personValidator;

    @Autowired
    public PersonController(PersonService personService, PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
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
    public String create(@ModelAttribute("person")@Valid  Person person, BindingResult bindingResult) {
        personValidator.validate(person,bindingResult);
        if (bindingResult.hasErrors()){
            return "person/creationPage";
        }

        personService.savePerson(person);
        return "redirect:/person";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", personService.findOne(id));
        return "person/changePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person")@Valid  Person person, BindingResult bindingResult) {
        personValidator.validate(person,bindingResult);
        if (bindingResult.hasErrors()){
            return "person/changePage";
        }
        personService.savePerson(person);
        return "redirect:/person";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personService.deletePerson(id);
        return "redirect:/person";
    }
}
