package ru.vavilov.notebook6.codesage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.codesage.service.GitExportService;

@Controller
@RequiredArgsConstructor
public class ExportController {

    private final GitExportService gitExportService;

    @GetMapping("/codesaga/export")
    public String showExportForm() {
        return "codesaga/exportCode";
    }

    @PostMapping("/codesaga/export")
    public String handleExport(
        @RequestParam(required = false) String repoUrl,
        @RequestParam(required = false) MultipartFile file,
        Model model) {

        String exportedCode;

        if (file != null && !file.isEmpty()) {
            exportedCode = gitExportService.exportCodeFromZip(file);
        } else if (repoUrl != null && !repoUrl.trim().isEmpty()) {
            exportedCode = gitExportService.exportCodeFromRepo(repoUrl.trim());
        } else {
            exportedCode = "Ошибка: укажите ссылку на репозиторий или загрузите архив.";
        }

        model.addAttribute("exportedCode", exportedCode);
        return "codesaga/exportCode";
    }

}
