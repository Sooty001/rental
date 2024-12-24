package com.example.rental.dto;

public class AgreementsStatDto {
    int countReviews;
    int countAllAgreement;

    public AgreementsStatDto(int countReviews, int countAllAgreement) {
        this.countReviews = countReviews;
        this.countAllAgreement = countAllAgreement;
    }

    public int getCountReviews() {
        return countReviews;
    }
    public void setCountReviews(int countReviews) {
        this.countReviews = countReviews;
    }

    public int getCountAllAgreement() {
        return countAllAgreement;
    }
    public void setCountAllAgreement(int countAllAgreement) {
        this.countAllAgreement = countAllAgreement;
    }
}
