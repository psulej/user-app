package dev.psulej.userapp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    Long id;
    String firstName;
    String lastName;
    String login;
    String email;

    public User(Long id, String firstName, String lastName, String login, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
