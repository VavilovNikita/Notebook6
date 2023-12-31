package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "notebook")
public class Notebook{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "notebook_seq")
    @SequenceGenerator(name = "notebook_seq", initialValue = 28)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "title")
    @NotEmpty(message = "Поле заголовок не может быть пустым")
    private String title;
    @Column(name = "text", columnDefinition="text")
    @NotEmpty(message = "Поле текст не может быть пустым")
    private String text;
    @Column(name = "position")
    private int position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notebook notebook = (Notebook) o;
        return id == notebook.id && position == notebook.position && Objects.equals(user, notebook.user) && Objects.equals(title, notebook.title) && Objects.equals(text, notebook.text) && Objects.equals(createdAt, notebook.createdAt) && Objects.equals(updatedAt, notebook.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, title, text, position, createdAt, updatedAt);
    }
}
