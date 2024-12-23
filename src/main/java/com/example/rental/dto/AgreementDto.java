package com.example.rental.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class AgreementDto implements Serializable {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double rentAmount;
    private int clientId;
    private int propertyId;
    private int reviewId;

    public AgreementDto(LocalDate startDate, LocalDate endDate, double rentAmount, int clientId, int propertyId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.clientId = clientId;
        this.propertyId = propertyId;
    }

    public AgreementDto() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getRentAmount() {
        return rentAmount;
    }
    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getReviewId() {
        return reviewId;
    }
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
}
