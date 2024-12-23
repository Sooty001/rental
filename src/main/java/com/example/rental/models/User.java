package com.example.rental.models;

import com.example.rental.enums.UserRoles;
import com.example.rental.exceptions.EmailAlreadyInUseException;
import com.example.rental.exceptions.InvalidParameterException;
import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User extends BaseEntityId {
    private String email;
    private String password;
    private UserRoles role;
    private Client client;
    private Agent agent;

    public User(String email, String uniqueEmail, String password, String hashPassword, UserRoles role) {
        setEmail(email, uniqueEmail);
        setPassword(password, hashPassword);
        setRole(role);
    }

    protected User() {}

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email.isBlank() || !email.contains("@")) {
            throw new InvalidParameterException(email);
        }
        this.email = email;
    }
    public void setEmail(String email, String uniqueEmail) {
        if (email.isBlank() || !email.contains("@")) {
            throw new InvalidParameterException(email);
        } else if (uniqueEmail != null) {
            throw new EmailAlreadyInUseException(uniqueEmail);
        }
        this.email = email;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPassword(String password, String hashPassword) {
        if (password.isBlank() || password.length() < 6 || password.length() > 16) {
            throw new InvalidParameterException("пароля");
        }
        this.password = hashPassword;
    }

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    public UserRoles getRole() {
        return role;
    }
    public void setRole(UserRoles role) {
        this.role = role;
    }

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    public Agent getAgent() {
        return agent;
    }
    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
