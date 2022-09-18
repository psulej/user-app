package dev.psulej.userapp;

public class User {

    Long id;
    String firstName;
    String lastName;
    String login;
    String mail;

    public User(Long id, String firstName, String lastName, String login, String mail) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.mail = mail;
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

    public String getMail() {
        return mail;
    }
}
