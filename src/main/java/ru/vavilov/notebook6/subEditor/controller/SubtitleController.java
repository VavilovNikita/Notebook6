package ru.vavilov.notebook6.subEditor.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.subEditor.model.Language;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.service.MovieService;
import ru.vavilov.notebook6.subEditor.service.SubtitleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subeditor")
public class SubtitleController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private SubtitleService subtitleService;

    @PostMapping("/upload")
    public String uploadSubtitle(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Файл пустой");
            return "subtitles/upload";
        }

        try {
           Movie movie = movieService.parseSubtitles(file, session.getId());

            model.addAttribute("movie", movie);
            model.addAttribute("subtitles", movie.getSubtitles());
            model.addAttribute("flagCodes", Map.of(
                "russian", "ru",
                "english", "us"
            ));
            return "subtitles/subtitles";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обработке файла: " + e.getMessage());
            return "subtitles/upload";
        }
    }

    @GetMapping("/subtitles/{id}/edit")
    public String editSubtitle(@PathVariable("id") Long id, Model model) {
           Movie movie = new Movie().setSubtitles(new ArrayList<>(Collections.singletonList(
               subtitleService.getSubtitleById(id))));
            model.addAttribute("movie", movie);
            return "subtitles/subtitles";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "subtitles/upload";
    }

    @GetMapping("/movie/{id}")
    public String getMovieById(Model model, @PathVariable("id") Long id) {
        model.addAttribute("movie", movieService.getMovieById(id));
        model.addAttribute("languages", Language.values());
        return "subtitles/movie";
    }

    @GetMapping("/movies")
    public String getAllMovies(Model model, HttpSession session) {
        List<Movie> movies = movieService.getAllMovie(session.getId());
        if (movies == null) movies = new ArrayList<>();
        model.addAttribute("movies", movies);
        return "subtitles/movies";
    }

    @PostMapping("/translate/{movieId}/{languageId}")
    public String translateText(Model model,
                                @PathVariable("movieId") Long movieId,
                                @PathVariable("languageId") Long languageId) {
        Movie movie = movieService.translateAndSaveMovie(movieId, languageId);
        model.addAttribute("movie", movie);
        return "subtitles/subtitles";
    }
}