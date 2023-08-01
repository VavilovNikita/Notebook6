package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notebook")
public class Notebook {
    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String text;

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

    @Override
    public String toString() {
        return "notebook{" +
                "id=" + id +
                ", head='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
