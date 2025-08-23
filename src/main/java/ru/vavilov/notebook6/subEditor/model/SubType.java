package ru.vavilov.notebook6.subEditor.model;

import lombok.Getter;

@Getter
public enum SubType {
    SRT(".srt"),
    ASS(".ass"),
    SSA(".ssa"),
    VTT(".vtt");

    private final String extension;

    SubType(String extension) {
        this.extension = extension;
    }

    public static SubType getTypeFromName(String name) {
        for (SubType subType : values()) {
            if (name.contains(subType.getExtension())){
                return subType;
            }
        }
        return ASS;
    }
}
