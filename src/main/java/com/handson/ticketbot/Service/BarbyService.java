package com.handson.ticketbot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.ticketbot.model.NewProductsBarby;
import com.handson.ticketbot.repo.NewProductsBarbyRepo;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarbyService {
    @Autowired
    ObjectMapper om;

    @Autowired
    NewProductsBarbyRepo newProductsBarbyRepo;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            getProduct(); // Fetch data and save to database
        } catch (IOException e) {
            System.err.println("Failed to fetch initial data: " + e.getMessage());
        }
    }
    public String searchProduct(String keyword) throws IOException {
        List<NewProductsBarby> shows = newProductsBarbyRepo.findAll();

        if (shows.isEmpty())
            return null;
        List<NewProductsBarby> filteredShowList = shows.stream()
                .filter(show -> show.getShowName().contains(keyword))
                .collect(Collectors.toList());

        String ret = "";
        for(int i = 0 ; i < filteredShowList.size(); ++i){
            if("Available".equals(filteredShowList.get(i).getShowSoldStatus())){
                ret += filteredShowList.get(i).getShowName() + " at " + filteredShowList.get(i).getShowDate() + " " +
                        filteredShowList.get(i).getShowTime() + " in Barby Tel-Aviv. Order tickets: " +
                        filteredShowList.get(i).getShowUrl();
                ret += "\n";
            }
        }
        return ret;
    }

    @Transactional
    public void getProduct() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://barby.co.il/api/shows/find")
                .method("GET", null)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Accept-Language", "en-US,en;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "_fbp=fb.2.1729541942090.997064977560564717")
                .addHeader("If-None-Match", "W/\"9fa6-j6LtIf8mRmk52BWA9T9UMTWk7vg\"")
                .addHeader("Referer", "https://barby.co.il/")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (responseBody.isEmpty()) {
                return;
            }
            ShowResponse res = om.readValue(responseBody, ShowResponse.class);
            newProductsBarbyRepo.deleteAll();
            for (NewProductsBarby show : res.getShow()) {
                newProductsBarbyRepo.save(show);
            }
        }
    }

    static class ShowResponse{

        List<NewProductsBarby> show;
        public List<NewProductsBarby> getShow() {
            return show;
        }
    }

}
