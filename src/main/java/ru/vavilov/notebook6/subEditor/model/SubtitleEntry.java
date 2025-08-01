package ru.vavilov.notebook6.subEditor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtitleEntry {
    private LocalTime start;
    private LocalTime end;
    private String text;
}
