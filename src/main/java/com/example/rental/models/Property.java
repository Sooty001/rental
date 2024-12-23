package com.example.rental.models;

import com.example.rental.exceptions.InvalidParameterException;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "properties")
public class Property extends BaseEntityId {
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
    private String PhotoUrl;
    private boolean isDeleted;
    private Agent agent;
    private Set<Agreement> agreements;

    public Property(String city, String street, int houseNumber, int floor, int apartmentNumber, double distanceToCenter, double square, int rooms, double price, String status, String photoUrl) {
        setCity(city);
        setStreet(street);
        setHouseNumber(houseNumber);
        setFloor(floor);
        setApartmentNumber(apartmentNumber);
        setDistanceToCenter(distanceToCenter);
        setSquare(square);
        setRooms(rooms);
        setPrice(price);
        setStatus(status);
        setRating(0);
        setPhotoUrl(photoUrl);
        setIsDeleted(false);
    }

    protected Property() {}

    @Column(name = "city", nullable = false)
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        if (city.isBlank() || city.length() < 2) {
            throw new InvalidParameterException(city);
        }
        this.city = city;
    }

    @Column(name = "street", nullable = false)
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        if (street.isBlank() || street.length() < 2) {
            throw new InvalidParameterException(street);
        }
        this.street = street;
    }

    @Column(name = "house_number", nullable = false)
    public int getHouseNumber() {
        return houseNumber;
    }
    public void setHouseNumber(int houseNumber) {
        if (houseNumber <= 0) {
            throw new InvalidParameterException(String.valueOf(houseNumber));
        }
        this.houseNumber = houseNumber;

    }

    @Column(name = "floor")
    public int getFloor() {
        return floor;
    }
    public void setFloor(int floor) {
        if (floor <= 0) {
            throw new InvalidParameterException(String.valueOf(floor));
        }
        this.floor = floor;
    }

    @Column(name = "apartment_number")
    public int getApartmentNumber() {
        return apartmentNumber;
    }
    public void setApartmentNumber(int apartmentNumber) {
        if (apartmentNumber <= 0) {
            throw new InvalidParameterException(String.valueOf(apartmentNumber));
        }
        this.apartmentNumber = apartmentNumber;
    }

    @Column(name = "distanceToCenter", nullable = false)
    public double getDistanceToCenter() {
        return distanceToCenter;
    }
    public void setDistanceToCenter(double distanceToCenter) {
        if (distanceToCenter <= 0) {
            throw new InvalidParameterException(String.valueOf(distanceToCenter));
        }
        this.distanceToCenter = distanceToCenter;
    }

    @Column(name = "square", nullable = false)
    public double getSquare() {
        return square;
    }
    public void setSquare(double square) {
        if (square <= 0) {
            throw new InvalidParameterException(String.valueOf(square));
        }
        this.square = square;
    }

    @Column(name = "rooms", nullable = false)
    public int getRooms() {
        return rooms;
    }
    public void setRooms(int rooms) {
        if (rooms <= 0) {
            throw new InvalidParameterException(String.valueOf(rooms));
        }
        this.rooms = rooms;
    }

    @Column(name = "price", nullable = false)
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        if (price <= 0) {
            throw new InvalidParameterException(String.valueOf(price));
        }
        this.price = price;
    }

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        if (!status.matches("^(Available|Rented)$")) {
            throw new InvalidParameterException(status);
        }
        this.status = status;
    }

    @Column(name = "rating")
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", referencedColumnName = "id")
    public Agent getAgent() {
        return agent;
    }
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    public Set<Agreement> getAgreements() {
        return agreements;
    }
    public void setAgreements(Set<Agreement> agreements) {
        this.agreements = agreements;
    }
}

