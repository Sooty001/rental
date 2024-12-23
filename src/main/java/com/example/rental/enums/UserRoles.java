package com.example.rental.enums;

public enum UserRoles {
    CLIENT("CLIENT"), AGENT("AGENT"), ADMIN("ADMIN");

    private final String value;

    UserRoles(String client) {
        this.value = client;
    }

    public String getValue() {
        return value;
    }
}
