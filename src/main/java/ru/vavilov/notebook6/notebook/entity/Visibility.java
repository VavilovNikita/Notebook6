package ru.vavilov.notebook6.notebook.entity;

public enum Visibility {
    /** Visible only to the owner. Default for every note. */
    PERSONAL,
    /** Visible read-only to every authenticated user; only the owner can edit/delete it. */
    TEAM
}
