package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;

@Entity
@Table(name = "notebook")
public class Notebook {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;
    @Column(name = "title")
    @NotEmpty(message = "Поле заголовок не может быть пустым")
    private String title;
    @Column(name = "text")
    @NotEmpty(message = "Поле текст не может быть пустым")
    private String text;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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
