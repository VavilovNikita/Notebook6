package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @NotEmpty(message = "Поле Имя не должно быть пустым")
    @Column(name = "firstName")
    private String firstName;
    @NotEmpty(message = "Поле Фамилия не должно быть пустым")
    @Column(name = "secondName")
    private String secondName;
    @Column(name = "email")
    @Email(message = "Неверный формат Email")
    @NotEmpty(message = "Поле email не должно быть пустым")
    private String email;
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy") // дд/мм/гггг
    @NotNull(message = "Поле email не должно быть пустым")
    private Date dateOfBirth;
    @OneToMany(mappedBy = "person")
    private List<Notebook> notes;

    public Person(int id, String firstName, String secondName, String email, Date dateOfBirth, List<Notebook> notes) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
