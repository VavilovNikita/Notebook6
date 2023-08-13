package ru.vavilov.notebook6.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "role")
    private List<User> userRoles;

    public Role(int id, String role, List<User> userRoles) {
        this.id = id;
        this.role = role;
        this.userRoles = userRoles;
    }

    public Role(String role) {
        this.role = role;
    }

    public Role() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<User> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<User> userRoles) {
        this.userRoles = userRoles;
    }
}
