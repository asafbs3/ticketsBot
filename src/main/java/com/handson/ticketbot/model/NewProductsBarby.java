package com.handson.ticketbot.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.handson.ticketbot.Service.BarbyService;

import javax.persistence.*;

@Entity
@Table(name="barbyProducts")
@JsonDeserialize(builder = NewProductsBarby.Builder.class)
public class NewProductsBarby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String showDate;
    private String showId;
    private String showName;
    private int showSold;
    private String showSoldMaxBuy;
    private String showSoldStatus;
    private String showUrl;
    private String showTime;

    public NewProductsBarby() {
        // No-args constructor for JPA
    }

    // Private constructor
    private NewProductsBarby(Builder builder) {
        this.showDate = builder.showDate;
        this.showId = builder.showId;
        this.showName = builder.showName;
        this.showSold = builder.showSold;
        this.showSoldMaxBuy = builder.showSoldMaxBuy;
        this.showTime = builder.showTime;
        // Automatically set showUrl and showSoldStatus
        this.setShowUrl(builder.showId);
        this.setShowSoldStatus();
    }
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String showDate;
        private String showId;
        private String showName;
        private int showSold;
        private String showSoldMaxBuy;
        private String showTime;

        public Builder setShowDate(String showDate) {
            this.showDate = showDate;
            return this;
        }
        public Builder setShowId(String showId) {
            this.showId = showId;
            return this;
        }

        public Builder setShowName(String showName) {
            this.showName = showName;
            return this;
        }

        public Builder setShowSold(int showSold) {
            this.showSold = showSold;
            return this;
        }

        public Builder setShowSoldMaxBuy(String showSoldMaxBuy) {
            this.showSoldMaxBuy = showSoldMaxBuy;
            return this;
        }

        public Builder setShowTime(String showTime) {
            this.showTime = showTime;
            return this;
        }

        // Build method to create an instance of ShowResponseObject
        public NewProductsBarby build() {
            return new NewProductsBarby(this);
        }
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showId) {
        this.showUrl = "https://barby.co.il/show/" + showId;
    }
    public String getShowDate() {
        return showDate;
    }

    public String getShowId() {
        return showId;
    }

    public String getShowName() {
        return showName;
    }

    public int getShowSold() {
        return showSold;
    }

    public String getShowSoldMaxBuy() {
        return showSoldMaxBuy;
    }
    public void setShowSoldStatus(){
        if (this.showSold >= Integer.parseInt(this.showSoldMaxBuy))
            this.showSoldStatus = "Sold out";
        else
            this.showSoldStatus = "Available";
    }
    public String getShowSoldStatus() {return showSoldStatus;}
    public String getShowTime() {
        return showTime;
    }

}
