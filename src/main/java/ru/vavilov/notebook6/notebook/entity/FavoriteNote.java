package ru.vavilov.notebook6.notebook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A (user, notebook) pairing marking a note as favorited by that user.
 * Kept as its own join entity rather than a boolean on Notebook because
 * favorites are per-user, not a property of the note itself - this matters
 * once notes can be shared (TEAM visibility) and viewed by many users.
 */
@Entity
@Table(name = "favorite_note", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "notebook_id"}))
public class FavoriteNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "notebook_id", referencedColumnName = "id", nullable = false)
    private Notebook notebook;

    public FavoriteNote() {
    }

    public FavoriteNote(User user, Notebook notebook) {
        this.user = user;
        this.notebook = notebook;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }
}
