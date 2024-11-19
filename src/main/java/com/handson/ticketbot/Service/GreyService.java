package com.handson.ticketbot.Service;

import com.handson.ticketbot.repo.NewProductsGreyRepo;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.handson.ticketbot.model.NewProductGrey;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GreyService {

    @Autowired
    NewProductsGreyRepo newProductsGreyRepo;
    public static final Pattern LINK_PATTERN = Pattern.compile("div class=\".*?article-list\">\\s*<a href=\"([^\"]+)\"");
    public static final Pattern PRODUCT_PATTERN = Pattern.compile("<h2 class=\\\"club-title\\\">([^<]+)</h2>\\s*<div class=\\\"singer-name\\\">\\s*([^<]+)</div>\\s*<div class=\\\"date-time\\\">\\s*([^<]+)</div>");

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            parseProductHtml(getProductHtml()); // Fetch data and save to database
        } catch (IOException e) {
            System.err.println("Failed to fetch initial data: " + e.getMessage());
        }
    }

    public String searchProducts(String keyword) throws IOException {
        List<NewProductGrey> shows = newProductsGreyRepo.findAll();
        if (shows.isEmpty())
            return null;
        List<NewProductGrey> filteredShowList = shows.stream()
                .filter(show -> show.getName().contains(keyword))
                .collect(Collectors.toList());
        String ret = "";
        for(int i = 0; i < filteredShowList.size(); ++i){
            System.out.println(filteredShowList.get(i).getId());
            ret += filteredShowList.get(i).getName() + " at " + filteredShowList.get(i).getTime() + " in " +
                  filteredShowList.get(i).getVenue() + ". Order tickets: " + filteredShowList.get(i).getUrl() + "\n";
        }
        return ret;
    }

    private void parseProductHtml(String html) {
        if (html == null)
            return;

        Matcher matcherLink = LINK_PATTERN.matcher(html);
        Matcher matcherProduct = PRODUCT_PATTERN.matcher(html);

        newProductsGreyRepo.deleteAll();
        while (matcherLink.find() && matcherProduct.find()) {
            String productUrl = matcherLink.group(1);
            if (!newProductsGreyRepo.existsByUrl(productUrl)) {
                NewProductGrey product = NewProductGrey.NewProductGreyBuilder.aNewProductGrey()
                        .name(matcherProduct.group(2).trim())
                        .time(matcherProduct.group(3).trim())
                        .url(productUrl)
                        .venue(matcherProduct.group(1))
                        .build();
                newProductsGreyRepo.save(product);
            }
        }
    }
    @Transactional
    private String getProductHtml() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://grayclub.co.il/")
                .method("GET", null)
                .addHeader("authority", "grayclub.co.il")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cache-control", "max-age=0")
                .addHeader("cookie", "__cf_bm=xJOqika0CT.c4NHpccJDC.qG9bwp.MIyXFyPV36H_Uc-1729855749-1.0.1.1-y7uf3EV2DqrnPcFOvcfNiVxZYJXwSNvukCktX_abHW8hXTNh.KoYmdZWv4WJ4ue39KrCQWZZLjztRyyI81DGoQ; _gcl_au=1.1.856627724.1729855750; _pk_ref.f833bfcc-d5fd-4a3f-8e01-b6e4b1880171.f2b3=%5B%22%22%2C%22%22%2C1729855751%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.f833bfcc-d5fd-4a3f-8e01-b6e4b1880171.f2b3=*; _ga=GA1.1.44778134.1729855751; _fbp=fb.2.1729855751003.907447446505092622; _hjSessionUser_2672368=eyJpZCI6ImIwNzE2YjQ0LWZhNzItNTU3Ny05NTQyLTFjYmNiMDlhNzkyYSIsImNyZWF0ZWQiOjE3Mjk4NTU3NTEzOTcsImV4aXN0aW5nIjpmYWxzZX0=; _hjSession_2672368=eyJpZCI6ImI3NDM0NWE5LThiZmYtNDJkMi1iZjA2LWVkM2QzYTViODI1MSIsImMiOjE3Mjk4NTU3NTE0MDgsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjoxLCJzcCI6MH0=; home-popup=true; PHPSESSID=6ggtkri55htstrk12i8dg1jhih; _ga_FZ0L9F6XBD=GS1.1.1729855750.1.1.1729855779.0.0.0; _pk_id.f833bfcc-d5fd-4a3f-8e01-b6e4b1880171.f2b3=b6589fc6ab0dc82c.1729855751.1.1729855806.1729855751.")
                .addHeader("referer", "https://www.google.com/")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "cross-site")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }
            return responseBody;
        }
    }
}
