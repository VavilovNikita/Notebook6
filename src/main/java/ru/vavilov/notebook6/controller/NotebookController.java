package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.Notebook;
import ru.vavilov.notebook6.service.AuthService;
import ru.vavilov.notebook6.service.NotebookService;


@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;
    private final AuthService authService;



    @Autowired
    public NotebookController(NotebookService notebookService, AuthService authService) {
        this.notebookService = notebookService;
        this.authService = authService;
    }
    @GetMapping()
    public String getNotes(Model model,@RequestParam(defaultValue = "",required = false)String allNotes,
                           @RequestParam(defaultValue = "0",required = false) Integer page,
                           @RequestParam(defaultValue = "15",required = false)Integer size) {
        model.addAttribute("authUser", authService.getUser());
        if(allNotes.isBlank()){
            model.addAttribute("notebook", authService.getUser().getNotes());
        }else{
            model.addAttribute("notebook", notebookService.findAll(PageRequest.of(page, size)));
            model.addAttribute("allNones",allNotes);
            model.addAttribute("page",page);
        }
        return "notebook/allNotesPage";
    }

    @GetMapping("/{id}")
    public String noteInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", notebookService.findById(id));
        model.addAttribute("authUser", authService.getUser());
        return "notebook/noteInfoPage";
    }

    @GetMapping("/new")
    public String newNote(@ModelAttribute("notebook") Notebook notebook,Model model) {
        model.addAttribute("authUser", authService.getUser());
        return "notebook/createNotePage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult,Model model) {
        model.addAttribute("authUser", authService.getUser());
        if (bindingResult.hasErrors()) {
            return "notebook/createNotePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String editNote(Model model, @PathVariable("id") int id) {
        model.addAttribute("notebook", notebookService.findById(id));
        model.addAttribute("authUser", authService.getUser());
        return "notebook/changeNotePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult,Model model) {
        model.addAttribute("authUser", authService.getUser());
        if (bindingResult.hasErrors()) {
            return "notebook/changeNotePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable("id") int id) {
        notebookService.deleteNotebook(id);
        return "redirect:/notebook";
    }
}
