package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "notebook")
public class Notebook {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "title")
    @NotEmpty(message = "Поле заголовок не может быть пустым")
    private String title;
    @Column(name = "text")
    @NotEmpty(message = "Поле текст не может быть пустым")
    private String text;
    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.DATE)
    private LocalDate updatedAt;

    public Notebook(int id, String head, String text) {
        this.id = id;
        this.title = head;
        this.text = text;
    }

    public Notebook() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String head) {
        this.title = head;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "notebook{" +
                "id=" + id +
                ", head='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
