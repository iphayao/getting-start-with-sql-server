package com.sqlsamples;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<Task>();

    public User() {

    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTask(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "User [id=" + this.id + ", name=" + this.getFullName() + "]";
    }

}