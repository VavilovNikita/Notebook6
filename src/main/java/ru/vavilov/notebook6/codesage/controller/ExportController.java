package ru.vavilov.notebook6.codesage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String handleExport(@RequestParam String repoUrl, Model model) {
        String exportedCode = gitExportService.exportCodeFromRepo(repoUrl);
        model.addAttribute("exportedCode", exportedCode);
        return "codesaga/exportCode";
    }
}
