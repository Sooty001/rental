package com.example.rental.dto;

public class ReviewDto {
    int id;
    private String comment;
    private int agentRating;
    private int propertyRating;
    int agreementId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getAgentRating() {
        return agentRating;
    }
    public void setAgentRating(int agentRating) {
        this.agentRating = agentRating;
    }

    public int getPropertyRating() {
        return propertyRating;
    }
    public void setPropertyRating(int propertyRating) {
        this.propertyRating = propertyRating;
    }

    public int getAgreementId() {
        return agreementId;
    }
    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }
}
