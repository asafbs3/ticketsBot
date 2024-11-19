package com.handson.ticketbot.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.persistence.*;

@Entity
@Table(name="eventerProducts")
@JsonDeserialize(builder = NewProductsEventer.Builder.class)
public class NewProductsEventer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String _id;
    private String url;
    private String title;

    public NewProductsEventer() {
        // No-args constructor for JPA
    }

    public String get_id() {
        return _id;
    }
    private NewProductsEventer(Builder builder) {
        this._id = builder._id;
        this.title = builder.title;
        this.url = builder.url;

    }
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String _id;
        private  String url;
        private String title;

        public Builder set_id(String _id) {
            this._id = _id;
            return this;
        }
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        // Build method to create an instance of ShowResponseObject
        public NewProductsEventer build() {
            return new NewProductsEventer(this);
        }
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return "https://www.eventer.co.il/events" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
