package com.handson.ticketbot.model;

import javax.persistence.*;

@Entity
@Table(name="product")
public class NewProductZappa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String status;
    private String url;
    private String time;
    private String venue;
    private String city;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static final class NewProductBuilder {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String status;
        private String url;
        private String time;
        private String venue;
        private String city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        private NewProductBuilder() {
        }

        public static NewProductBuilder aNewProduct() {
            return new NewProductBuilder();
        }

        public NewProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewProductBuilder status(String status) {
            this.status = status;
            return this;
        }

        public NewProductBuilder url(String url) {
            this.url = url;
            return this;
        }

        public NewProductBuilder time(String time) {
            this.time = time;
            return this;
        }

        public NewProductBuilder venue(String venue) {
            this.venue = venue;
            return this;
        }

        public NewProductBuilder city(String city) {
            this.city = city;
            return this;
        }

        public NewProductZappa build() {
            NewProductZappa newProduct = new NewProductZappa();
            newProduct.setName(name);
            newProduct.setStatus(status);
            newProduct.setUrl(url);
            newProduct.setTime(time);
            newProduct.setVenue(venue);
            newProduct.setCity(city);
            return newProduct;
        }
    }
}
