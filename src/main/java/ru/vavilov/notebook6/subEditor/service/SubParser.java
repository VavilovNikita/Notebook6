package ru.vavilov.notebook6.subEditor.service;

import ru.vavilov.notebook6.subEditor.model.Subtitle;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SubParser {

    public static List<SubtitleEntry> parseASS(InputStream inputStream, String language) throws IOException {
        List<Subtitle> entries = new ArrayList<>();
        List<SubtitleEntry> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

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
                // Парсим время в миллисекунды
                long startMillis = parseTimeToMillis(startStr);
                long endMillis = parseTimeToMillis(endStr);

                text = text.replaceAll("\\{.*?\\}", "");
                text = text.replace("\\N", "\n");

                Subtitle subtitle = new Subtitle()
                    .setStartMillis(startMillis)
                    .setEndMillis(endMillis)
                    .setText(text);

                entries.add(subtitle);
            } catch (Exception e) {
                System.err.println("Ошибка парсинга строки: " + line + " " + e);
            }
        }

        SubtitleEntry entry = new SubtitleEntry()
            .setLanguage(language);

        for (Subtitle subtitle : entries) {
            subtitle.setSubtitleEntry(entry);
            entry.getSubtitles().add(subtitle);
        }

        result.add(entry);
        return result;
    }

    // Вспомогательный метод для парсинга времени в миллисекунды
    private static long parseTimeToMillis(String timeStr) {
        // Формат: 0:00:00.00 или 0:00:00.000
        String[] parts = timeStr.split("[:.]");

        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid time format: " + timeStr);
        }

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = 0;

        if (parts.length > 3) {
            String msPart = parts[3];
            if (msPart.length() == 1) {
                milliseconds = Integer.parseInt(msPart) * 100;
            } else if (msPart.length() == 2) {
                milliseconds = Integer.parseInt(msPart) * 10;
            } else {
                milliseconds = Integer.parseInt(msPart.substring(0, 3));
            }
        }

        return ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L + milliseconds;
    }
}