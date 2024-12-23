package com.example.rental.models;

import com.example.rental.exceptions.InvalidParameterException;
import com.example.rental.exceptions.ValidateDateRangeException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "agreements")
public class Agreement extends BaseEntityId {
    private LocalDate startDate;
    private LocalDate endDate;
    private double rentAmount;
    private boolean isDeleted;
    private Client client;
    private Property property;
    private Review review;

    public Agreement(LocalDate startDate, LocalDate endDate, double rentAmount) {
        setStartDate(startDate);
        setEndDate(endDate, startDate);
        setRentAmount(rentAmount);
        setIsDeleted(false);
    }

    protected Agreement() {}

    @Column(name = "start_date", nullable = false)
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date", nullable = false)
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void setEndDate(LocalDate endDate, LocalDate startDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidParameterException("дат");
        }
        else if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new ValidateDateRangeException();
        }
        this.endDate = endDate;
    }

    @Column(name = "rent_amount", nullable = false)
    public double getRentAmount() {
        return rentAmount;
    }
    public void setRentAmount(double rentAmount) {
        if (rentAmount <= 0) {
            throw new InvalidParameterException(String.valueOf(rentAmount));
        }
        this.rentAmount = rentAmount;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

    @OneToOne(mappedBy = "agreement", fetch = FetchType.LAZY)
    public Review getReview() {
        return review;
    }
    public void setReview(Review review) {
        this.review = review;
    }
}

