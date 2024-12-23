package com.example.rental.dto;

import java.io.Serializable;

public class PropertyDto implements Serializable {
    int id;
    private String city;
    private String street;
    private int houseNumber;
    private int floor;
    private int apartmentNumber;
    private double distanceToCenter;
    private double square;
    private int rooms;
    private double price;
    private String status;
    private double rating;
    private String photoUrl;
    private int agentId;

    public PropertyDto(String city, String street, int houseNumber, int floor, int apartmentNumber, double distanceToCenter, double square, int rooms, double price, String status, double rating, String photoUrl, int agentId) {
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.floor = floor;
        this.apartmentNumber = apartmentNumber;
        this.distanceToCenter = distanceToCenter;
        this.square = square;
        this.rooms = rooms;
        this.price = price;
        this.status = status;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.agentId = agentId;
    }

    public PropertyDto() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }
    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getFloor() {
        return floor;
    }
    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getApartmentNumber() {
        return apartmentNumber;
    }
    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public double getDistanceToCenter() {
        return distanceToCenter;
    }
    public void setDistanceToCenter(double distanceToCenter) {
        this.distanceToCenter = distanceToCenter;
    }

    public double getSquare() {
        return square;
    }
    public void setSquare(double square) {
        this.square = square;
    }

    public int getRooms() {
        return rooms;
    }
    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getAgentId() {
        return agentId;
    }
    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }
}
