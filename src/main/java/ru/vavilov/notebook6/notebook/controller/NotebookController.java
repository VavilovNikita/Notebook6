package ru.vavilov.notebook6.notebook.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.Tag;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.entity.Visibility;
import ru.vavilov.notebook6.notebook.service.FavoriteService;
import ru.vavilov.notebook6.notebook.service.NotebookService;
import ru.vavilov.notebook6.notebook.search.SearchField;
import ru.vavilov.notebook6.notebook.service.AuthService;
import ru.vavilov.notebook6.notebook.service.SearchService;
import ru.vavilov.notebook6.notebook.service.TagService;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/notebook")
public class NotebookController {
    private final NotebookService notebookService;
    private final AuthService authService;
    private final SearchService searchService;
    private final TagService tagService;
    private final FavoriteService favoriteService;
    private final String URL = "/notebook";


    @Autowired
    public NotebookController(NotebookService notebookService, AuthService authService, SearchService searchService,
                               TagService tagService, FavoriteService favoriteService) {
        this.notebookService = notebookService;
        this.authService = authService;
        this.searchService = searchService;
        this.tagService = tagService;
        this.favoriteService = favoriteService;
    }

    @GetMapping()
    public String getNotes(Model model, @RequestParam(defaultValue = "", required = false) String allNotes,
                           @RequestParam(defaultValue = "mine", required = false) String view,
                           @RequestParam(required = false) String tag,
                           @RequestParam(defaultValue = "0", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size) {
        User currentUser = authService.getUser();
        model.addAttribute("authUser", currentUser);
        model.addAttribute("search", new SearchField());
        model.addAttribute("URL", URL);
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("selectedTag", tag);
        model.addAttribute("favoriteIds", favoriteService.getFavoriteNotebookIds());

        if (!allNotes.isBlank()) {
            // Legacy "every note from every user" pagination view - bypasses PERSONAL
            // visibility entirely, so it's restricted to admins now that visibility means
            // something. Regular users get the "Библиотека команды" tab instead.
            if (!authService.isAdmin()) {
                throw new AccessDeniedException("Просмотр всех заметок доступен только администратору");
            }
            model.addAttribute("notebook", notebookService.findAll(PageRequest.of(page, size)));
            model.addAttribute("allNones", allNotes);
            model.addAttribute("page", page);
            model.addAttribute("view", "all");
            return "notebook/allNotesPage";
        }

        List<Notebook> notes;
        if ("favorites".equals(view)) {
            notes = favoriteService.getFavoriteNotes();
        } else if ("team".equals(view)) {
            notes = notebookService.getTeamLibrary(tag);
        } else {
            view = "mine";
            notes = notebookService.getMyNotes(tag);
        }
        model.addAttribute("notebook", notes);
        model.addAttribute("view", view);
        return "notebook/allNotesPage";
    }

    @GetMapping("/search")
    public String noteInfo(@ModelAttribute("search") SearchField searchField, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("notebook", searchService.getNotesByTitleOrText(searchField.getText()));
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("favoriteIds", favoriteService.getFavoriteNotebookIds());
        return "notebook/allNotesPage";
    }

    @GetMapping("/{id}")
    public String noteInfo(@PathVariable("id") int id, Model model) {
        Notebook notebook = notebookService.findById(id);
        User currentUser = authService.getUser();
        boolean isOwner = notebook != null && notebook.getUser() != null
                && notebook.getUser().getId() == currentUser.getId();
        if (notebook == null || (!isOwner && notebook.getVisibility() != Visibility.TEAM)) {
            throw new AccessDeniedException("Нет прав на просмотр этой заметки");
        }
        model.addAttribute("notebook", notebook);
        model.addAttribute("authUser", currentUser);
        model.addAttribute("search", new SearchField());
        return "notebook/noteInfoPage";
    }

    @GetMapping("/new")
    public String newNote(@ModelAttribute("notebook") Notebook notebook, Model model) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("tagsCsv", "");
        return "notebook/createNotePage";
    }

    @PostMapping()
    public String create(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult, Model model,
                          @RequestParam(required = false) String tagNames) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", tagService.getAllTags());
            model.addAttribute("tagsCsv", tagNames);
            return "notebook/createNotePage";
        }
        notebookService.saveNotebook(notebook, tagNames);
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
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("tagsCsv", notebook.getTags().stream()
                .map(Tag::getName).sorted().collect(Collectors.joining(", ")));
        return "notebook/changeNotePage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("notebook") @Valid Notebook notebook, BindingResult bindingResult, Model model,
                          @RequestParam(required = false) String tagNames) {
        model.addAttribute("authUser", authService.getUser());
        model.addAttribute("search", new SearchField());
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", tagService.getAllTags());
            model.addAttribute("tagsCsv", tagNames);
            return "notebook/changeNotePage";
        }
        notebookService.saveNotebook(notebook, tagNames);
        return "redirect:/notebook";
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable("id") int id) {
        notebookService.deleteNotebook(id);
        return "redirect:/notebook";
    }

    @PostMapping("/{id}/favorite")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFavorite(@PathVariable("id") int id) {
        boolean nowFavorite = favoriteService.toggleFavorite(id);
        return ResponseEntity.ok(nowFavorite);
    }
}
