package com.example.rental.models;

import com.example.rental.exceptions.InvalidParameterException;
import com.example.rental.exceptions.PhoneAlreadyInUseException;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "clients")
public class Client extends BaseEntityId {
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String PhotoUrl;
    private boolean isDeleted;
    private Set<Agreement> agreements;
    private User user;

    public Client(String firstName, String lastName, String middleName, String phone, String uniquePhone, String photoUrl) {
        setFirstName(firstName);
        setLastName(lastName);
        setMiddleName(middleName);
        setPhone(phone, uniquePhone);
        setPhotoUrl(photoUrl);
        setIsDeleted(false);
    }

    protected Client() {}

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if (firstName.isBlank() || firstName.length() < 2) {
            throw new InvalidParameterException(firstName);
        }
        this.firstName = firstName;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if (lastName.isBlank() || lastName.length() < 2) {
            throw new InvalidParameterException(lastName);
        }
        this.lastName = lastName;
    }

    @Column(name = "middle_name")
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        if (middleName != null && !middleName.isBlank() && middleName.length() < 2) {
            throw new InvalidParameterException(middleName);
        }
        this.middleName = middleName;
    }

    @Column(name = "phone", nullable = false, unique = true)
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        if (!phone.matches("^(\\+7|8)\\d{10}$")) {
            throw new InvalidParameterException(phone);
        }
        this.phone = phone;
    }
    public void setPhone(String phone, String uniquePhone) {
        if (!phone.matches("^(\\+7|8)\\d{10}$")) {
            throw new InvalidParameterException(phone);
        } else if (uniquePhone != null) {
            throw new PhoneAlreadyInUseException(phone);
        }
        this.phone = phone;
    }

    @Column(name = "photoUrl")
    public String getPhotoUrl() {
        return PhotoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    public Set<Agreement> getAgreements() {
        return agreements;
    }
    public void setAgreements(Set<Agreement> agreements) {
        this.agreements = agreements;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}

