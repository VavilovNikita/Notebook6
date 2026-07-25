package ru.vavilov.notebook6.codesage.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GitExportService {

    private static final Set<String> ALLOWED_HOSTS = Set.of("github.com", "gitlab.com");

    public String exportCodeFromRepo(String repoUrl) {
        try {
            validateRepoUrl(repoUrl);
        } catch (IllegalArgumentException e) {
            log.warn("Отклонён запрос на экспорт репозитория: {}", e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
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

    public String exportCodeFromZip(MultipartFile zipFile) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("zip-extract-");

            File zip = File.createTempFile("upload-", ".zip");
            zipFile.transferTo(zip);

            unzip(zip, tempDir.toFile());

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
                        log.warn("Не удалось прочитать файл: {}", path, e);
                    }
                });

            return codeBuilder.length() > 0 ? codeBuilder.toString() : "Java-файлы не найдены в архиве";

        } catch (IOException e) {
            log.error("Ошибка при обработке архива", e);
            return "Ошибка при обработке архива: " + e.getMessage();
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

    private void validateRepoUrl(String repoUrl) {
        if (repoUrl == null || repoUrl.isBlank()) {
            throw new IllegalArgumentException("Ссылка на репозиторий не указана");
        }
        URL url;
        try {
            url = new URL(repoUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Некорректная ссылка на репозиторий");
        }
        if (!"https".equalsIgnoreCase(url.getProtocol())) {
            throw new IllegalArgumentException("Разрешены только https-ссылки");
        }
        String host = url.getHost() == null ? "" : url.getHost().toLowerCase(Locale.ROOT);
        if (!ALLOWED_HOSTS.contains(host)) {
            throw new IllegalArgumentException("Хост '" + host + "' не входит в список разрешённых (" + ALLOWED_HOSTS + ")");
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    private void unzip(File zipFile, File destDir) throws IOException {
        String destDirCanonicalPath = destDir.getCanonicalPath();
        try (java.util.zip.ZipInputStream zipIn = new java.util.zip.ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                String outFileCanonicalPath = outFile.getCanonicalPath();
                if (!outFileCanonicalPath.startsWith(destDirCanonicalPath + File.separator)
                        && !outFileCanonicalPath.equals(destDirCanonicalPath)) {
                    throw new IOException("Обнаружена попытка Zip Slip: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    if (!outFile.isDirectory() && !outFile.mkdirs()) {
                        throw new IOException("Не удалось создать директорию: " + outFile);
                    }
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        if (!parent.mkdirs()) {
                            throw new IOException("Не удалось создать директорию: " + parent);
                        }
                    }
                    try (java.io.OutputStream outStream = Files.newOutputStream(outFile.toPath())) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            outStream.write(buffer, 0, len);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        }
    }
}
