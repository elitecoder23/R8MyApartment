package com.example.androidexample;

public class Friend {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String phoneNumber;

    public Friend(String username, String firstName, String lastName, String email, String birthDate, String phoneNumber) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}