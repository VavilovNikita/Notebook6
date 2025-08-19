package ru.vavilov.notebook6.subEditor.service;

import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class SubParser {

    public static List<SubtitleEntry> parseSRT(InputStream inputStream) throws IOException {
        List<SubtitleEntry> entries = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder textBuilder = new StringBuilder();
        LocalTime start = null;
        LocalTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                if (start != null && end != null && textBuilder.length() > 0) {
                    entries.add(new SubtitleEntry(start, end, textBuilder.toString().trim()));
                    textBuilder.setLength(0);
                    start = null;
                    end = null;
                }
                continue;
            }

            // Номер строки — пропускаем
            if (line.matches("\\d+")) {
                continue;
            }

            // Временная строка: start --> end
            if (line.contains("-->")) {
                String[] times = line.split("-->");
                start = LocalTime.parse(times[0].trim(), formatter);
                end = LocalTime.parse(times[1].trim(), formatter);
                continue;
            }

            // Текст
            textBuilder.append(line).append("\n");
        }

        // Последняя запись
        if (start != null && end != null && textBuilder.length() > 0) {
            entries.add(new SubtitleEntry(start, end, textBuilder.toString().trim()));
        }

        return entries;
    }

    public static List<SubtitleEntry> parseASS(InputStream inputStream) throws IOException {
        List<SubtitleEntry> entries = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss.SS");

        int startIndex = 0;
        int endIndex = 0;
        int textIndex = 0;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("Format:")) {
                String cleanLine = line.substring("Format:".length()).trim();
                String[] format = cleanLine.split(",", 10);
                for (int i = 0; i < format.length; i++) {
                    switch (format[i].trim().toLowerCase()) {
                        case "start" -> startIndex = i;
                        case "end" -> endIndex = i;
                        case "text" -> textIndex = i;
                    }
                }
            }
            if (!line.startsWith("Dialogue:")) {
                continue;
            }

            String cleanLine = line.substring("Dialogue:".length()).trim();
            String[] parts = cleanLine.split(",", 10);
            if (parts.length < 10) continue;

            String startStr = parts[startIndex].trim();
            String endStr = parts[endIndex].trim();
            String text = parts[textIndex].trim();

            try {
                LocalTime start = LocalTime.parse(startStr, formatter);
                LocalTime end = LocalTime.parse(endStr, formatter);

                text = text.replaceAll("\\{.*?\\}", "");
                text = text.replace("\\N", "\n");

                entries.add(new SubtitleEntry(start, end, text));
            } catch (Exception e) {
                System.err.println("Ошибка парсинга строки: " + line + e);
            }
        }

        return entries;
    }
}