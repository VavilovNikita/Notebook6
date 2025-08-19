package ru.vavilov.notebook6.subEditor.controller;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;
import ru.vavilov.notebook6.subEditor.service.MovieService;
import ru.vavilov.notebook6.subEditor.service.SubParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subeditor")
public class SubtitleController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MovieService movieService;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    @PostMapping("/upload")
    public String uploadSubtitle(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Файл пустой");
            return "subtitles/upload";
        }

        try {
            String originalFilename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();  // Читаем байты один раз

            // Для MinIO
            InputStream uploadStream = new java.io.ByteArrayInputStream(bytes);
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(originalFilename)
                    .stream(uploadStream, bytes.length, -1)
                    .contentType(file.getContentType())
                    .build()
            );

            String fileUrl = minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(originalFilename)
                    .extraQueryParams(Map.of("response-content-disposition", "attachment"))
                    .build()
            );

            // Для парсера
            InputStream parseStream = new java.io.ByteArrayInputStream(bytes);
            List<SubtitleEntry> subtitles = SubParser.parseASS(parseStream);

            // Сохраняем в БД
            Movie movie = movieService.saveMovie(originalFilename, fileUrl, subtitles);

            // Добавляем в модель
            model.addAttribute("movie", movie);
            model.addAttribute("subtitles", subtitles);

            return "subtitles/subtitles";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обработке файла: " + e.getMessage());
            return "subtitles/upload";
        }
    }

    // Если нужна отдельная GET для загрузки
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