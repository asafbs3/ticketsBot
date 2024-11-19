package com.handson.ticketbot.model;

import javax.persistence.*;

@Entity
@Table(name="grey_product")
public class NewProductGrey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String time;
    private String venue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }


    public static final class NewProductGreyBuilder {
        private Long id;
        private String name;
        private String url;
        private String time;
        private String venue;

        private NewProductGreyBuilder() {
        }

        public static NewProductGreyBuilder aNewProductGrey() {
            return new NewProductGreyBuilder();
        }

        public NewProductGreyBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NewProductGreyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewProductGreyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public NewProductGreyBuilder time(String time) {
            this.time = time;
            return this;
        }

        public NewProductGreyBuilder venue(String venue) {
            this.venue = venue;
            return this;
        }

        public NewProductGrey build() {
            NewProductGrey newProductGrey = new NewProductGrey();
            newProductGrey.setId(id);
            newProductGrey.setName(name);
            newProductGrey.setUrl(url);
            newProductGrey.setTime(time);
            newProductGrey.setVenue(venue);
            return newProductGrey;
        }
    }
}
