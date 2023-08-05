package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "firstName")

    private String firstName;
    @Column(name = "secondName")
    private String secondName;
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "person")
    private List<Notebook> notes;

    public Person(int id, String firstName, String secondName, String email, List<Notebook> notes) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.notes = notes;
    }
    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Notebook> getNotes() {
        return notes;
    }

    public void setNotes(List<Notebook> notes) {
        this.notes = notes;
    }
}
