package ru.vavilov.notebook6.subEditor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SubtitleEntry {
    @Id
    private int index;
    private LocalTime start;
    private LocalTime end;
    @Column(columnDefinition = "TEXT")
    private String text;
}
