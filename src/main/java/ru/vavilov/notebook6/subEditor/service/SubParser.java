package ru.vavilov.notebook6.subEditor.service;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import ru.vavilov.notebook6.subEditor.model.Subtitle;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

    public static List<SubtitleEntry> parseSRT(InputStream inputStream, String language) throws IOException {
        List<Subtitle> entries = new ArrayList<>();
        List<SubtitleEntry> result = new ArrayList<>();

        byte[] fileBytes = inputStream.readAllBytes();
        Charset charset = detectCharset(fileBytes);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(fileBytes), charset));
        String line;

        StringBuilder currentText = new StringBuilder();
        long startMillis = 0;
        long endMillis = 0;
        boolean readingText = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) {
                if (readingText && currentText.length() > 0) {
                    Subtitle subtitle = new Subtitle()
                        .setStartMillis(startMillis)
                        .setEndMillis(endMillis)
                        .setText(currentText.toString().trim());

                    entries.add(subtitle);

                    currentText = new StringBuilder();
                    readingText = false;
                }
                continue;
            }

            if (line.matches("^\\d+$")) {
                continue;
            }

            if (line.contains("-->")) {
                String[] timeParts = line.split("-->");
                if (timeParts.length == 2) {
                    startMillis = parseSRTTimeToMillis(timeParts[0].trim());
                    endMillis = parseSRTTimeToMillis(timeParts[1].trim());
                    readingText = true;
                }
                continue;
            }

            if (readingText) {
                if (currentText.length() > 0) {
                    currentText.append("\n");
                }
                currentText.append(line);
            }
        }

        if (currentText.length() > 0) {
            Subtitle subtitle = new Subtitle()
                .setStartMillis(startMillis)
                .setEndMillis(endMillis)
                .setText(currentText.toString().trim());

            entries.add(subtitle);
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

    private static long parseTimeToMillis(String timeStr) {
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

    private static long parseSRTTimeToMillis(String timeStr) {
        String normalizedTime = timeStr.replace(',', '.');
        String[] parts = normalizedTime.split("[:.]");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid SRT time format: " + timeStr);
        }

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = Integer.parseInt(parts[3]);

        return ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L + milliseconds;
    }

    private static Charset detectCharset(byte[] bytes) {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(bytes);
        CharsetMatch match = detector.detect();
        if (match == null) {
            return java.nio.charset.StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(match.getName());
        } catch (Exception e) {
            return java.nio.charset.StandardCharsets.UTF_8;
        }
    }
}