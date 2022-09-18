
package dev.psulej.userapp;

public class BasicUser {

    Long id;
    String firstName;
    String lastName;

    public BasicUser(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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
}
