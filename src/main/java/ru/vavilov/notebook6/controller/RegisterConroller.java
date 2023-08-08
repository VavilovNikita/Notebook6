package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vavilov.notebook6.entity.Person;
import ru.vavilov.notebook6.service.PersonService;

@Controller
public class RegisterConroller {

    PersonService personService;

    @Autowired
    public RegisterConroller(PersonService personService) {
        this.personService = personService;
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("person",new Person());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("person") Person person) {
        personService.savePerson(person);
        return "redirect:/notebook";
    }
}
