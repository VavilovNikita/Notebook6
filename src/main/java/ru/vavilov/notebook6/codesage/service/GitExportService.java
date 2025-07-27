package ru.vavilov.notebook6.codesage.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GitExportService {

    public String exportCodeFromRepo(String repoUrl) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("git-clone-");

            log.info("Cloning repo: {}", repoUrl);
            Git.cloneRepository()
                .setURI(repoUrl + ".git")
                .setDirectory(tempDir.toFile())
                .call();

            StringBuilder codeBuilder = new StringBuilder();
            Files.walk(tempDir)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
                .forEach(path -> {
                    codeBuilder.append("// ").append(path.getFileName()).append("\n");
                    try {
                        String content = Files.readAllLines(path).stream()
                            .collect(Collectors.joining("\n"));
                        codeBuilder.append(content).append("\n\n");
                    } catch (IOException e) {
                        log.warn("Cannot read file: {}", path, e);
                    }
                });

            return codeBuilder.length() > 0 ? codeBuilder.toString() : "Файлы не найдены или пустой репозиторий";

        } catch (IOException | GitAPIException e) {
            log.error("Ошибка при экспорте репозитория", e);
            return "Ошибка при экспорте репозитория: " + e.getMessage();
        } finally {
            if (tempDir != null) {
                try {
                    deleteDirectoryRecursively(tempDir);
                } catch (IOException e) {
                    log.warn("Не удалось удалить временную директорию", e);
                }
            }
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
}
