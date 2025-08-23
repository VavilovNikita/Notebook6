package ru.vavilov.notebook6.subEditor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.service.MovieService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subeditor")
public class SubtitleController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/upload")
    public String uploadSubtitle(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Файл пустой");
            return "subtitles/upload";
        }

        try {
           Movie movie = movieService.parseSubtitles(file);

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

    @GetMapping("/upload")
    public String uploadPage() {
        return "subtitles/upload";
    }

    @GetMapping("/movie/{id}")
    public String getMovieById(Model model, @PathVariable("id") Long id) {
        model.addAttribute("movie", movieService.getMovieById(id));
        return "subtitles/movie";
    }

    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.getAllMovie();
        if (movies == null) movies = new ArrayList<>();
        model.addAttribute("movies", movies);
        return "subtitles/movies";
    }
}