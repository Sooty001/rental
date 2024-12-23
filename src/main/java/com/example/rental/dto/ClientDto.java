package com.example.rental.dto;

public class ClientDto {
    int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String email;
    private String PhotoUrl;

    public ClientDto(String firstName, String lastName, String middleName, String phone, String email, String photoUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
        PhotoUrl = photoUrl;
    }

    public ClientDto() {}

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
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
