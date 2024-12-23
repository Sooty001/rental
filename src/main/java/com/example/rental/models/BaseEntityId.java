package com.example.rental.models;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntityId {
    private int id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
