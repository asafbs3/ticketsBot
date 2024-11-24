package com.handson.ticketbot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.ticketbot.model.NewProductsEventer;
import com.handson.ticketbot.repo.NewProductsEventerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import okhttp3.*;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventerService {

    @Autowired
    ObjectMapper om;

    @Autowired
    NewProductsEventerRepo newProductsEventerRepo;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            getProduct(); // Fetch data and save to database
        } catch (IOException e) {
            System.err.println("Failed to fetch initial data: " + e.getMessage());
        }
    }

    // Scheduled API call to refresh data every hour (3,600,000 ms)
    @Scheduled(fixedRate = 3600000)
    public void updateShowsFromAPI() throws IOException {
        try {
            getProduct(); // Refresh data from API periodically
        } catch (IOException e){
            System.err.println("Failed to refresh data " + e.getMessage());
        }
    }

    public String searchProduct(String keyword) throws IOException {
        List<NewProductsEventer> shows = newProductsEventerRepo.findAll();
        if (shows.isEmpty())
            return null;
        List<NewProductsEventer> filteredShowList = shows.stream()
                .filter(show -> show.getTitle().contains(keyword))
                .collect(Collectors.toList());
        String ret = "";
        for(int i = 0; i < filteredShowList.size(); ++i){
            ret += filteredShowList.get(i).getTitle() + ". Order tickets: " + filteredShowList.get(i).getUrl() + "\n";
        }
        return ret;
    }

    public void getProduct() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://www.eventer.co.il/sliders/categories/live_concerts/events.js")
                .method("GET", null)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (responseBody.isEmpty()) {
                return;
            }
            EventResponse res = om.readValue(responseBody, EventResponse.class);
            newProductsEventerRepo.deleteAll();
            for (NewProductsEventer show : res.getSlides()) {
                newProductsEventerRepo.save(show);
            }
        }
    }

    static class EventResponse{
        List<NewProductsEventer> slides;
        public List<NewProductsEventer> getSlides() {
            return slides;
        }
    }
}
