package ru.vavilov.notebook6.subEditor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/subeditor")
public class SubtitleController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSubtitle(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пустой");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();

            // Сохраняем файл в minio

            // TODO: здесь можно парсить и обрабатывать файл

            return ResponseEntity.ok("Файл успешно загружен: " + originalFilename);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при обработке файла");
        }
    }
}

