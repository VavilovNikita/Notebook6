package ru.vavilov.notebook6.notebook.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.service.NotebookService;
import ru.vavilov.notebook6.notebook.search.SearchField;
import ru.vavilov.notebook6.notebook.service.AuthService;
import ru.vavilov.notebook6.notebook.service.SearchService;


@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;
    private final AuthService authService;
    private final SearchService searchService;
    private final String URL = "/notebook";


    @Autowired
    public NotebookController(NotebookService notebookService, AuthService authService, SearchService searchService) {
        this.notebookService = notebookService;
        this.authService = authService;
        this.searchService = searchService;
    }

    @GetMapping()
    public String getNotes(Model model, @RequestParam(defaultValue = "", required = false) String allNotes,
                           @RequestParam(defaultValue = "0", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        model.addAttribute("URL", URL);
        if (allNotes.isBlank()) {
            model.addAttribute("notebook", authService.getUser().getNotes());
        } else {
            model.addAttribute("notebook", notebookService.findAll(PageRequest.of(page, size)));
            model.addAttribute("allNones", allNotes);
            model.addAttribute("page", page);
        }
        return "notebook/allNotesPage";
    }

    @GetMapping("/search")
    public String noteInfo(@ModelAttribute("search") SearchField searchField, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("notebook", searchService.getNotesByTitleOrText(searchField.getText()));
        return "notebook/allNotesPage";
    }

    @GetMapping("/{id}")
    public String noteInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("notebook", notebookService.findById(id));
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        return "notebook/noteInfoPage";
    }

    @GetMapping("/new")
    public String newNote(@ModelAttribute("notebook") Notebook notebook, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        return "notebook/createNotePage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        if (bindingResult.hasErrors()) {
            return "notebook/createNotePage";
        }
        notebookService.saveNotebook(notebook);
        return "redirect:/notebook";
    }

    @GetMapping("/{id}/edit")
    public String editNote(Model model, @PathVariable("id") int id) {
        Notebook notebook = notebookService.findById(id);
        if (notebook == null || notebook.getUser() == null
                || notebook.getUser().getId() != authService.getUser().getId()) {
            throw new AccessDeniedException("Нет прав на редактирование этой заметки");
        }
        model.addAttribute("notebook", notebook);
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        return "notebook/changeNotePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
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
