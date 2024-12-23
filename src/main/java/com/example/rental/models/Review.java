package com.example.rental.models;

import com.example.rental.exceptions.InvalidParameterException;
import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntityId {
    private String comment;
    private int agentRating;
    private int propertyRating;
    private boolean isDeleted;
    private Agreement agreement;

    public Review(String comment, int agentRating, int propertyRating) {
        setComment(comment);
        setAgentRating(agentRating);
        setPropertyRating(propertyRating);
        setIsDeleted(false);
    }

    protected Review() {}

    @Column(name = "comment")
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        if (comment.isBlank()) {
            throw new InvalidParameterException(comment);
        }
        this.comment = comment;
    }

    @Column(name = "agent_rating")
    public int getAgentRating() {
        return agentRating;
    }
    public void setAgentRating(int agentRating) {
        if (agentRating <= 0 || agentRating > 5) {
            throw new InvalidParameterException(String.valueOf(agentRating));
        }
        this.agentRating = agentRating;
    }

    @Column(name = "property_rating")
    public int getPropertyRating() {
        return propertyRating;
    }
    public void setPropertyRating(int propertyRating) {
        if (propertyRating <= 0 || propertyRating > 5) {
            throw new InvalidParameterException(String.valueOf(propertyRating));
        }
        this.propertyRating = propertyRating;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", updatable = false)
    public Agreement getAgreement() {
        return agreement;
    }
    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }
}
