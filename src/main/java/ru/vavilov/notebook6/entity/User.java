package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "userTable")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Поле Имя пользователя не должно быть пустым")
    @Column(name = "user_name")
    private String username;
    @NotEmpty(message = "Поле Имя пользователя не должно быть пустым")
    @Column(name = "password")
    private String password;
    @NotEmpty(message = "Поле Имя не должно быть пустым")
    @Column(name = "first_Name")
    private String firstName;
    @NotEmpty(message = "Поле Фамилия не должно быть пустым")
    @Column(name = "last_Name")
    private String lastName;
    @Column(name = "email")
    @Email(message = "Неверный формат Email")
    @NotEmpty(message = "Поле email не должно быть пустым")
    private String email;
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy") // дд/мм/гггг
    @NotNull(message = "Поле email не должно быть пустым")
    private Date dateOfBirth;
    @OneToMany(mappedBy = "user")
    private List<Notebook> notes;

    public User(int id, String firstName, String secondName, String email, Date dateOfBirth, List<Notebook> notes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = secondName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.notes = notes;
    }

    public User() {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String secondName) {
        this.lastName = secondName;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
