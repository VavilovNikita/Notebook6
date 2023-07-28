package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notebook")
public class Notebook {
    @Id
    @GeneratedValue
    private int id;
    private String head;
    private String text;

    public Notebook(int id, String head, String text) {
        this.id = id;
        this.head = head;
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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
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
                ", head='" + head + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
