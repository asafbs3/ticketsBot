package com.handson.ticketbot.Service;

import com.handson.ticketbot.model.NewProductZappa;
import org.springframework.stereotype.Service;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ZappaService {

    public static final Pattern PRODUCT_PATTERN = Pattern.compile("\\{\"productGroupId\":\"([^\"]+)\",\"name\":\"([^\"]+)\",\"startDate\":\"([^\"]+)\",\"endDate\":\"([^\"]+)\",\"productCount\":([^,]+),\"link\":\"([^\"]+)\",.*?\"status\":\"([^\"]+)\",\"products\":");
    public static final Pattern HEADER_PATTERN = Pattern.compile("<h2 class=\"event-listing-city \" data-qa=\"list-event-main-info\">([^<]+)");
    public static final Pattern TICKETS_PATTERN = Pattern.compile("\"priceCurrency\":\".*?\",\"[^\"]+\":\"([^\"]+)\",\"url\":\"([^\"]+)\"");
    public static final Pattern TIME_PATTERN = Pattern.compile("qa=\"event-date-time\">\\n([^\\n]+)");
    public static final Pattern LOCATION_PATTERN = Pattern.compile("<li class=\"event-listing-venue\" data-qa=\"list-event-sub-info\">([^<]+)");
    public static final Pattern CITY_PATTERN = Pattern.compile("data-qa=\"list-event-venue\">([^<]+)");
    public static final Pattern ID_PATTERN = Pattern.compile("<div class=\"listing-item listing-item-clickable\" onclick=\"location\\.xhref='([^']+)'");

    public List<NewProductZappa> searchProducts(String keyword) throws IOException {
        return parseProductHtml(getProductHtml(keyword));
    }
    private List<NewProductZappa> parseProductHtml(String html) {
        List<NewProductZappa> products = new ArrayList<>();

        Matcher matcherHeader = HEADER_PATTERN.matcher(html);
        Matcher matcherTickets = TICKETS_PATTERN.matcher(html);
        Matcher matcherTime = TIME_PATTERN.matcher(html);
        Matcher matcherLocation = LOCATION_PATTERN.matcher(html);
        Matcher matcherCity = CITY_PATTERN.matcher(html);

        while (matcherHeader.find() && matcherTickets.find() && matcherTime.find() && matcherLocation.find() && matcherCity.find()) {
            NewProductZappa product = NewProductZappa.NewProductBuilder.aNewProduct()
                    .name(matcherHeader.group(1))
                    .status(matcherTickets.group(1))
                    .url(matcherTickets.group(2))
                    .time(matcherTime.group(1))
                    .venue(matcherLocation.group(1))
                    .city(matcherCity.group(1))
                    .build();
            products.add(product);

        }
        return products;
    }

    private String getProductHtml(String keyword) throws IOException {
        System.out.println(keyword);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "en=page_view&ep.artist_available_event=1&ep.consent_settings=__1_2_3__&ep.env_affiliate_abbrv=ZPE&ep.env_affiliate_id=13674&ep.env_affiliate_shop_type=1&ep.env_configuration_id=86&ep.env_country=IL&ep.env_currency=ILS&ep.env_language=iw&ep.env_partner_id=5058&ep.env_platform_id=12&ep.env_template=eventseries&ep.event_serie_ID=3157154&ep.event_series_organizer_id=1471754&ep.event_series_genre=%D7%94%D7%95%D7%A4%D7%A2%D7%95%D7%AA%20%D7%97%D7%99%D7%95%D7%AA&ep.event_series_name=%D7%A7%D7%95%D7%91%D7%99%20%D7%A4%D7%A8%D7%A5&ep.event_series_sub_genre=%D7%90%D7%9E%D7%A0%D7%99%D7%9D%20%D7%99%D7%A9%D7%A8%D7%90%D7%9C%D7%99%D7%9D&ep.event_series_tickets_available=1&ep.host=www.zappa-club.co.il&ep.page_sitecategory=artist&ep.page_type=%2Fartist%2Fon_tour&ep.perm_info=1718538146003869643A20102024A21102024&ep.url_parameter=affiliate%3DZPE&ep.referrer=https%3A%2F%2Fwww.google.com%2F&epn.source_timestamp=1729541711816&ep.user_session_id=CECDBC4452021F29739797FBC5506850&ep.artist_name=%D7%A7%D7%95%D7%91%D7%99%20%D7%A4%D7%A8%D7%A5&ep.debug_mode=false&ep.artist_id=476254&ep.page_uri=%2Fartist%2F%25D7%25A7%25D7%2595%25D7%2591%25D7%2599-%25D7%25A4%25D7%25A8%25D7%25A5%2F&ep.platform=Web&ep.page_offers=&ep.dedup_user_id=1718538146003869643&ep.content_group=artist&ep.user_logged=false&_et=168");
        Request request = new Request.Builder()
                .url("https://www.zappa-club.co.il/artist/" + keyword + "/?affiliate=ZPE")
                .method("POST", body)
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("accept-language", "he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7,lb;q=0.6")
                .addHeader("cookie", "__cmpiuid=6e1ac547bb24b7050805b5d2d61fcc53; __cmpcvcx48964=__s94_s23_s7_s26_s1409_s905__; __cmpcpcx48964=__1_2_3__; __cmpcvc=__s94_s23_s7_s26_s1409_s905__; __cmpcpc=__1_2_3__; _ga=GA1.1.313468203.1728832360; FPAU=1.3.1471167336.1729005270; webid=WlBFX05PX1NFU1NJT05fSVc=; PIM-SESSION-ID=cr6SQYlZdWSuwr3d; webshop=YW1vdW50PTAmYW1vdW50X2V2aWRzPTAmY3VycmVuY3lfY29kZT1JTFMmZGV2aWNlX2luZm89MCZrZXk9S0I5S1RyQTJKZ2tCJmtwc191cmw9Jmxhbmd1YWdlPWl3JmxvY2FsZT1pd19pbCZsb2dpbj0wJm1vZD0xJnJhbmRvbT01ODQ3NzEmcmVmZXJlcj1odHRwcyUzQSUyRiUyRnd3dy56YXBwYS1jbHViLmNvLmlsJTJGYXJ0aXN0JTJGRDc5M0Q3QTBENzk5LUQ3QTFEN0EwRDc5M0Q3QThEN0ExRDc5NUQ3OUYlMkYmcmVzZXJ2YXRpb25fZHVyYXRpb249MA==; initialReferrer=https://www.google.com/; permInfo=1718538146003869643A20102024A21102024; sbsd_o=26BEC4230F1D27F41A8DC9A320E9430334E6FFA821ED072E391C1219742EC455~sD8fV6HfkbyuX0tKODKlRTTvjB16nFxZ7JxQ1bTqK/bAnj7nWsHkh19d6UiAlPpojBsg2qsD6mlGKs6+IOe0oqk8aM1vH/oCwaiY3d9UCbe4ze+w2GRloNGa+/ztM+YlH3A9i6OGeZAVDv9XFD5pVCrWQnrGtvJzQF6/9SfYZqvkoe+kvmiAV0GUx0jAC349unh+03nhqwPQ2GHf3sUCy6vkt/9fuE+F3vv/3g+GnAJfmPUco53+I9z9PZKnaE8XR; sbsd_ss=ab8e18ef4e; _abck=CEDF76DFA72E7F7FB086192B0E0A6FD0~0~YAAQaWt7XGiGgJWSAQAACtO0sAzLT1ADE4p86VWY1A4hORJZIQgVhKcT3QUq7YcleGwTJjrXw9AIC8YnY0DwREURstxejnmeJbYOFiFSdDHraGJPu00GPkQkA5GLXxGkKyHFqeFZhrnz4ciFQONI/KKZCbyQcfFPdFLVWXoNK862UzyU6yjjkv7eq8UTpI9la5Vr3raTD6dwAeMypL0ceOss0NtflBQjAu+oARNFk2yjdFY72Pt5LSgCVpQlp+NP0YXPoX35SKabQDdnRCa3XkQ0FtbyIbsmpuPAcIKUgjEHRt9syQUTueAiTjhSOBQ9XOSzZysWoX2gKP7aavsdccS4FG475U4NoHtNLyguEJvnHbiRkq1AcTvhcLNHkOJJfndCcNCdOTyS2LEJZ3n6Rs6i3VCkhKlNM+Arm9o=~-1~-1~1729545093; ak_bmsc=58701B78E83303F0A2B2631D757113AA~000000000000000000000000000000~YAAQaWt7XG+GgJWSAQAAG9a0sBlViqrQC2xrlAN4xvF5H9fwtADzowA+tioDny1FwUUxQbPe4gKR8fRbZ8fLMiYlxacReremzElgHUqZtPSfq3uJaFgdLcBeHvPoDAWctcKF5Tebubm8NerwBN0KZviOAfd7jc6vInZMua8tbCuvK5TiZbcLdwHRyzEI4uMcSKOh7UiICvgSfNvFidPRrtV5D7F9tAeq9loPn6DrbQeuLDxNlHkL7oXEwoP1CrzoiaHvv74pBNdcC7VpNjtm+QCgiiHGHsshv6uuN2porh4vGnGbPCtlB2CEsrRPDidzsCfNpM857FAS6/UCQ8IKf8SaCudfB5rgtjL2kSh2jGtHbEMRAX+LiUals1T+eZpNm5vp11MQFbCGA8HDswL8QdnqDIrpWKL8r/LNSOWPrbtGCo2En3EzcKOgR/T4qPv4AQgah7RwNN+JGOri3ghHWgrUZ/cOGVh3E0dxtlA1jpcX2qsuFBiuADb3NA==; sbsd=sMzzDRvZDuw+9h9H/K2DROFWcSkIdTKudpPedIemRuCcfouSUy2m4dD2qoZPjJEVRwIrVQ1pzxv+kXnJsmE0mmiyomEe89JxmAaV3HMZ5gAqNPN6BnAxqrvyySa84s7iW+WpV2K+fbFtaWcVLbRzV3FuWFbB06zmCuZt79ezhRmYuKdyCDHfb/vWi6KCwEDVW; SameSite=None; bm_sz=795B95C041B219A2E8851C9925115CAB~YAAQdWt7XC5f1a6SAQAAmwO4sBkxo3imUf/929bAUDfpn/jJNmX0RW3jJO+As2SKGXWEByceOp39d62cLzZ33ji4aAY4eBdMn40hBAgxV8aLptCvWLuPVIp6VGTrvWrsQhzh6sru7RQAdrs1OgdOWpSr4Tb2E+Xps9Wa8uu6YRs9rNdkFXOCzCE+VVdlXjohxMdAwXStF2h59mDjZ5vO6F8x5ycayaOV8nrnhYpSNaXurAL3sUOUo0kSU2Lna0EUrLcWJDgpzOzA4RJg4le0W5/Rii0DKNFRK8uUN++2tLN3oCxJs3Mvorpwv5PEbunQNX1Bu1KEgVc8RlZmQqdZ6VMM5UoqPP+e/EPkNjt5BcHWtliDqqZZ3PGc6Puz9+9U9GzYiPwS30Jsskwbr9d76wzotKGpKCaxvddVixJGPBim9jPDf6ztsRqFVjpz7PfRMy+3yowP8CRrpMlBaeGaLd1FEg==~4474433~3753282; akavpau_eventim_production_short=1729541712~id=2457587f7b56317fd33220b5ff1e877d; bm_sv=8DD769E4DD5F4679FF00D451DDEA12E4~YAAQdWt7XI1f1a6SAQAAjhW4sBluB6ExyAhHNR5Hkm1EovdvYBpvVxAuE2yKriAbZ0Dw8dSCAKYWiLILNHZSrF3YTr6MKozQ0V4AR5LPx4NDtTvetiNOTv8WS4Zc/HdTq9J3kH/2pXrc7DAWHB9PBtmAA/h6y1Ngd9PFQc2QWjW5c+9e9gtpdasLrKf2w0SeKLxs4YaIDuGP34Txzg0EGd5i3GIDqbzv8nIN0Wa8RurvI5utWbZvWY9ZsEVG6HzhGFMQS52Rnw==~1; _ga_RGFWP01SDV=GS1.1.1729541494.9.1.1729541710.0.0.0; _abck=DFAB5262EA62D156502E93F128624C61~-1~YAAQRlvKF1+qaJWSAQAAxNi/sAwxb++XAVOOWiFL4dZwqy4/dGpvDolkgdBfjDTDWtmmP28wOYCPt1wU1nhls1mcMFP8HEiq1A9bI/Jl63pEqLhdnuugLDYcMWRBpS22EXUsAbaH4UmRaKHSLZBTBlCwTqVCVpJ7JRy8SfKZpJ+pS5Gx4bcEu5I0CnL7E7/Q+jLI0zSjG3Yg9V5wAa3ZyIW5kkGX7hk83hhW+GwhFhPfEvabT65evWKgy1JAnA2drXGRHeOOZ9E1PrIRbRIU3efWHj4O/3nQRaBDoyFNzDQ7nE7EHkLlCTwNQrY9QbO8MeYNsoH4ZJus9yj3pBBc0w8Zp9RreWErYu4X/1mKePzmR4hTg23DnuuvnILcwYsnwuZs8XoxTOpkYZ8dVhY3+vK3BjfD+mHSephZf1fc28CknA==~-1~-1~-1; ak_bmsc=8E3FC21582AB66C936AC9BBEFABB7FC8~000000000000000000000000000000~YAAQRlvKF2CqaJWSAQAAxNi/sBlNKNMvfl6ZZfswagGuIDFxcQbtSR9xkycHGiKBBjl8EJxkZOrHNURLNQ5KyGY8YC+qvPMRl99tZP1NDwSBL5HzfIjMeYMJrc37RDYP5nQDI/CZfp7nmQcjKrhaoeu64SPbQlVUeBSPF80RWf9CJHSvbRI4N7uLqixmRCRqIeqye7kvMdJwHyRSlSMHLPKzXW61gKNwMWqMIj5LWv8d9n6Gf/FSU9+YOYAlLRqq2vEETEMeJrfD2cAXOdm7ru5RrFkoVEl2mLeqrlfJNB+mrvraWSb2C0lNMIbbJ9rfhERt56S3yne2dhk56i1mS5Enf6RU3SzoN2SUdqHua5f/ySX0rDW1j87QDPGj2Oh6YDD6; bm_sz=6AA4F02702A198DD2ABE432DDB858BB8~YAAQRlvKF2KqaJWSAQAAxNi/sBko3DzAR556a3YKayIRyaD2HXFptPmE1yr9BXdEcb7HbmtKXxUZ7wvP/kN8XUqNJ1bEwEouZKnhZ5PrVz2Is6P26gULWZ+1sFUgWelJAn1v8+sFoEjp4U7V/Btux/npljzcQNDdOxW1RSh3fhQASCBdGc/lBTmuV9v5338KyfpxJiqwbAKbEA83p/V1Lf9ctUeprvRTj1FVvO7CRjkTgbVd+IylypaWGyDA7SKM2WZs6aFBUYCrT5RNVYXcx0lbHJM7GRrN2Hz0WnDTcRBCesvtnp+8rUwvlQYzgDXYbhk6EhyIz8gSvPYuYJh7DhRoKiBqMRaRyQBjiTMrFUCBUNhL0S0=~4343346~4538677; permInfo=1718538146003869643A19102024A20102024; sbsd=sm8vspNyaUXwJL4GHTtXNxxbFCzf37CdNZNAO3yRtu/wnq8JYKtCBjeQq4pJvqw7c3QGnFeqsA+mL0JGwXsQkC78PxgZnIcMoK3zQA3Bi5Zo+/L/hRkMCo+elc+U4oQ5rZ45cH9hFBgjibuh7MO6CG36MkSuJQ/Dk65SExcsa+4OdCuxBhib8w8ueT/fZfJbm; webid=WlBFX05PX1NFU1NJT05fSVc=; webshop=bG9naW49MCZhbW91bnQ9MCZyZXNlcnZhdGlvbl9kdXJhdGlvbj0wJmtwc191cmw9JmFtb3VudF9ldmlkcz0wJnJhbmRvbT02MzMyMjEmcmVmZXJlcj1odHRwcyUzQSUyRiUyRnd3dy56YXBwYS1jbHViLmNvLmlsJTJGJmtleT0yREZ1N0FjcGt6VGMmbW9kPTEmZGV2aWNlX2luZm89MCZsYW5ndWFnZT1pdyZsb2NhbGU9aXdfaWwmY3VycmVuY3lfY29kZT1JTFM=; ADRUM_BTa=R:29|g:16364bdc-11e6-42b9-b4d7-37c91d1f8d73|n:customer1_3aa627d9-4de0-48ca-a644-db85ae91343a; SameSite=None; akavpau_eventim_production_short=1729542220~id=842baa4b2a36dcf6136e0ab0b5980efa")
                .addHeader("priority", "u=0, i")
                .addHeader("referer", "https://www.zappa-club.co.il/events/%D7%94%D7%95%D7%A4%D7%A2%D7%95%D7%AA-%D7%97%D7%99%D7%95%D7%AA-51/")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7,lb;q=0.6")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "__cmpiuid=3ee03cd71fc91111aef1c6166a89be9d")
                .addHeader("Referer", "https://www.zappa-club.co.il/")
                .addHeader("Sec-Fetch-Dest", "script")
                .addHeader("Sec-Fetch-Mode", "no-cors")
                .addHeader("Sec-Fetch-Site", "cross-site")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .addHeader("range", "bytes=0-")
                .addHeader("content-type", "text/plain;charset=UTF-8")
                .addHeader("origin", "https://www.zappa-club.co.il")
                .build();
        Response response = client.newCall(request).execute();
        try {
            String responseBodyString = response.body().string();

            Matcher matcherUrl = ID_PATTERN.matcher(responseBodyString);
//        System.out.println("check1: " + responseBodyString);
            System.out.println(matcherUrl.find());

            if (!matcherUrl.find())
                return responseBodyString;
            else {
                System.out.println("check2");
                return getArtistProductsZappaHtml(matcherUrl.group(1));
            }
        }
        finally {
            response.close();
        }
    }

    private String getArtistProductsZappaHtml(String keyword) throws IOException {
        System.out.println(keyword);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "en=page_view&ep.artist_available_event=1&ep.consent_settings=__1_2_3__&ep.env_affiliate_abbrv=ZPE&ep.env_affiliate_id=13674&ep.env_affiliate_shop_type=1&ep.env_configuration_id=86&ep.env_country=IL&ep.env_currency=ILS&ep.env_language=iw&ep.env_partner_id=5058&ep.env_platform_id=12&ep.env_template=eventseries&ep.event_serie_ID=2789054&ep.event_series_organizer_id=42331%2C307609&ep.event_series_genre=%D7%94%D7%95%D7%A4%D7%A2%D7%95%D7%AA%20%D7%97%D7%99%D7%95%D7%AA&ep.event_series_name=%D7%93%D7%A0%D7%99%20%D7%A1%D7%A0%D7%93%D7%A8%D7%A1%D7%95%D7%9F&ep.event_series_sub_genre=%D7%90%D7%9E%D7%A0%D7%99%D7%9D%20%D7%99%D7%A9%D7%A8%D7%90%D7%9C%D7%99%D7%9D&ep.event_series_tickets_available=1&ep.host=www.zappa-club.co.il&ep.page_sitecategory=artist&ep.page_type=%2Fartist%2Fon_tour%2Flist&ep.perm_info=1718538146003869643A19102024A20102024&ep.url_parameter=&ep.referrer=No%20Referrer&epn.source_timestamp=1729434976687&ep.user_session_id=CECDBC4452021F29739797FBC5506850&ep.artist_name=%D7%93%D7%A0%D7%99%20%D7%A1%D7%A0%D7%93%D7%A8%D7%A1%D7%95%D7%9F&ep.debug_mode=false&ep.artist_id=457064&ep.page_uri=%2Fartist%2F%25D7%2593%25D7%25A0%25D7%2599-%25D7%25A1%25D7%25A0%25D7%2593%25D7%25A8%25D7%25A1%25D7%2595%25D7%259F%2F%25D7%2593%25D7%25A0%25D7%2599-%25D7%25A1%25D7%25A0%25D7%2593%25D7%25A8%25D7%25A1%25D7%2595%25D7%259F-2789054%2F&ep.platform=Web&ep.page_offers=&ep.dedup_user_id=1718538146003869643&ep.content_group=artist&ep.user_logged=false&_et=199");
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.zappa-club.co.il/" + keyword);
        builder.method("POST", body);
        builder.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        builder.addHeader("accept-language", "he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7,lb;q=0.6");
        builder.addHeader("cookie", "initialReferrer=; __cmpiuid=6e1ac547bb24b7050805b5d2d61fcc53; __cmpcvcx48964=__s94_s23_s7_s26_s1409_s905__; __cmpcpcx48964=__1_2_3__; __cmpcvc=__s94_s23_s7_s26_s1409_s905__; __cmpcpc=__1_2_3__; _ga=GA1.1.313468203.1728832360; FPAU=1.3.1471167336.1729005270; permInfo=1718538146003869643A19102024A20102024; webid=WlBFX05PX1NFU1NJT05fSVc=; PIM-SESSION-ID=cr6SQYlZdWSuwr3d; webshop=YW1vdW50PTAmYW1vdW50X2V2aWRzPTAmY3VycmVuY3lfY29kZT1JTFMmZGV2aWNlX2luZm89MCZrZXk9S0I5S1RyQTJKZ2tCJmtwc191cmw9Jmxhbmd1YWdlPWl3JmxvY2FsZT1pd19pbCZsb2dpbj0wJm1vZD0xJnJhbmRvbT01ODQ3NzEmcmVmZXJlcj1odHRwcyUzQSUyRiUyRnd3dy56YXBwYS1jbHViLmNvLmlsJTJGYXJ0aXN0JTJGRDc5M0Q3QTBENzk5LUQ3QTFEN0EwRDc5M0Q3QThEN0ExRDc5NUQ3OUYlMkYmcmVzZXJ2YXRpb25fZHVyYXRpb249MA==; _abck=CEDF76DFA72E7F7FB086192B0E0A6FD0~0~YAAQTGMmF5cfEJqSAQAAdk9LqgxMjJ1o2ddn4jQFyrRZp/Nfd8sdlzLxhewU0+WFNJzbYSMqLTCfgW2UmhK8nYsoZHelIyiXYlznJ30WiGGSxAgNFnBqrwxdPgldfGoy9S/PpYcXxdAOyB98adsEocnwyUZkycf8XYiokWBvhPEvoCCtJB/yVhRCSrjqYJ+ukJdiMq04z5kue0TBZshYsdDw43I3MQh8rhQZGVMxUErURQPXdPBjdgJhn70YL43cL4t7nuSxJeR/dqO8Fr5jkM0Le46/gnduG4nTS3EYXDGrJfpjak2f7q1MZi2xgKzSzemP9gxVqUGr9Dro5fa2aT0gy0tWmaKcP3IF6+M85uYSk2eyhLJVu5J6aZsZRtSHUYkywefgef2Dt6sp4+baQaV9CzGDSqKm0u+szYo=~-1~-1~1729437515; bm_mi=3439A62D80E1DEFB5AE29034B8DAE244~YAAQPlfIF03M3FGSAQAAiUhQqhm3CZeUMpLderVaMQCjNDO9qEsY5peR5sgdqinv4FkXQZA5Ah4jrPZ9ttJ9J6J6ab+UBxjUOy14XpabvlSJxexAI86iVstWdCP7ZQUEpi1h8YXPskgnsG4HN+DkCqWaMP3sasmNb+pxmc2LvC5ELVrqX2iqz5kfrY87yrPp83/WawHKuQ9Eif8t35/cWCPJPIjL1V2iqK3kzD1PalvgWpC8IdVFN8YxaAoAmczuQ8m+//u2rPJsq/0CxPHwX2J04vyqr+htQgGt6Nt0rUZSt6b267783mRwnHX29LYEAWs=~1; initialReferrer=https://www.google.com/; ak_bmsc=EF345C7369F18C0A844FF8BF3CAA2212~000000000000000000000000000000~YAAQPlfIF8jM3FGSAQAAcm9QqhmaOqrMzrzm+Ejckjql+tDursYUAw8tIsC8BfvmBh/Pose7dXmnAxQWJeXkH/ZHix6E/kxnRjWZjTtFwhUY/p6CE0DCmkHi8JW4utapSeZA4VV1Zdwcfpgw0zw6dyNPpa82+sMmUklF2RM1+XrGjxKTG1ZaVvW1SQXzaLFzkU5wMsZAwjKZ0ZAGMmsvna/9+uCPNtGSjhCY081v8T4k4UM8SvezPZfGOJMj2KArWd6vPeJgOgFJGQDwejNew3Cw91lp62HXzkP22m6XUFMXtCB+NiK2x+tDIYOmc2OWZJ7hp3Q1m+VP+vsOqPd0XIR0x4RBVnCrtUhkwjaJnD58Lp6HIimEWO4uMAq75M9rQmopfMWsVhUH0DQHqu/wH9EN327yrcO6V2Ep0XFeLqKM7O1STo+AR1laFnXtCLhsJwcuJDny/4+aqrmT4IDu3N67/ebi2hU5kLrBgJbhgGC617N2O7y5kqrRDlum4Rt2WQ==; sbsd_ss=ab8e18ef4e; sbsd_o=2EBDD88866CF6AB0076AFA41F1837D9B14E52F3E9F8E3DC5C648DDD6B37DDFAB~sLWt/XIixpC80NoMBClAYc98k1fp4niTJxaB9uWWY7yiSk93BO++E7NDnBRhxvqI0fvc2qKQ+2qNFs19B8UZ3PsDOu61GnoyM0l+wvY4RCy++sa8I5Apd5hrnda0pyUvA8K+ebke3tY52vSKkO00E8DtSNMGZairmfSjh84PipO+DM3+T+ZQP0eDoZEaPsfra7K1gB1Wn/mdXLIaSNe6HCIqGDo7dFxZg/RqSD804qpt5kXPO5LlbYKq5slvcyauK; sbsd=syto3j6R6QoYfS95lhj32LGQY/BAr5QacYBXnwG0RO8K5R8HnuUl5evWrrxCeMW/mzkWfDPW2ZbNVpX2pr0ssjk/VuaI7ArP1/bqTMYxqh5L6aQDOu9pgVcJ777SWK4H1NxJ1VmV/B74/2Xb0QtaOsNA7sVQbyhbVU4qZf/Thk7efxmZ+g9YXZq1ZnbyRc0+m; akavpau_eventim_production_short=1729434976~id=f3026b720a8a00a85ddb25b9e2224b3f; bm_sv=562599DE7472426F11A11C4177F34F07~YAAQPlfIF7Lo3FGSAQAAMGxbqhmIf+evuuc6m7Om5tk1Xa5oK2YSZH1hKofJsyQzxR8e4WzT7f1/BHoSE8mpSIoOviC/ublLm9CjcM7ONUUK6Hsg98vZGP8cBMwC9RZngYnOLTgClPgcTRXvcwlQgArIDOk1/8Y3DNED9MXHn6q/PgSN7q1jJU+0qlnBz/NArDbckbUMUmxfO1PnphNsBxxA8zJsmaf2Ac8Ig6POnt1+ZABtslD24EwCqYU8rcpEOwCqefq6sA==~1; bm_sz=4CBEE507170D5E9840BF14DBC71F74CC~YAAQPlfIF7Po3FGSAQAAMGxbqhlDV3Hu3MY6+2zemUURdqyXAClWPUWKmbtYwZDhKpUjMxeAA6QMBRxYMLQG+bxwxRw5nCu9na1esvrQK2nV/AkQpBl6mBErDylo3zbC62im80P/4X+DqjIPo0Yy2Luxtxc+9uHES5vf+rllEBxRPbkTKJGcoFWKmIXm+Jwc36YM1RDOUF/I2LP0NVBg3YNkJ5RHM5h5uIEvNHBm/PHdqEc+EDrnEp8TSllWqhdgecvLRPQkMUgywu+AkjcJKYdZQHH/l67NcWoR6jbn0U0dky/Xkot+wc3hTc8yoiOCYmyaZNT4w0lNUcMYOt+h1bnWVQi/2ZD6ipqJeIgHv4MKpqZkjT3Az693XOu6GDgbEK8cqoZ6Wy84xmldTmjY62ca5GwxBOYOXccEdJg2X8zUG+Uy9ofql3HVIxzugwEoB5ZjAAhJbYuRemWi3bhWO93SP/kpZlc=~4539698~3355189; _ga_RGFWP01SDV=GS1.1.1729419514.8.1.1729434972.0.0.0; _abck=D51C08AD8FE47D8B4ECBF436C7A6A386~-1~YAAQyHxCF1XePpaSAQAAXhdcqgzQ9p2nHqx/l42yDSDLkKFd9lcP4NIFSoqg60JYdRGVtdO9z/ZPsGYnkdAZkpJbmYCH8sGGFThbOiY6+sUcSDqDRzbV2VgCOkNup10lx5Kt1iCLbqyv1+zsUIsYmfLxanzerTp7Z5Hfh4sKWO1z602IAYN1la5KnFI+KZA5+kSf0HLBuUnMVlzILc+FYLsatuXkev7BU8Ml9COLSiYkFVI+HgGi6WZ5r0KVU2SjI8a4iwXl9iC+2o8PIeJx0BQ/BKCSGNWD5TI+MpjoL+3iD60OaRQx/0g5ELPsZvH8VCYyamXUDxZFC8RGBmyrpUfW5Ktuc/tTmWlgtz+E+NCdsufQNMKMDYIPKbkJvsxkg9pV+NbcCi8xv4A7D9f7AoUUqkv3SPAa7s1CwDFCofYGoA==~-1~-1~-1; ak_bmsc=1DE90EC34F19FA57790C67C0DDDED94D~000000000000000000000000000000~YAAQyHxCF1bePpaSAQAAXhdcqhmBbtD8z/ar2ahjMKRwJZEk1X+T2TXM2ErDGZICuCI1MTRO9IGpFaWALKh+4vGmNhc6I12oOU2Ef+hAsNcMZe4rr7lxN0b4kBVSV9Q53ASP65/11Ri/nt+0HfgyAyXcu9KMfYUPaXZOvlvPHuQOhuk0Rq+DTeUL0MH2Fpx2wM0EhC/XfssMXisikqrpU4uw+JgpmM/PeWpTXQ2GZSe8uqfzib4zoWvgAElLroe0VeMNvrmulbzUWqPn9RQduzRXwHpK9qlkwjNZJIyS9jMkQsUa2ej8Y9XQ6NgYpHLHgVMACaeR4NJviXWuu3iH3kN5xO4wq5XrgNqMRpiTKRnM0LA5FRU15Cvlm/Fvmq8RHQ==; bm_sz=2865996A0534A99E14FEE1CE78DB649F~YAAQyHxCF1jePpaSAQAAXxdcqhlcRFsCYPoaulm4bZ/07aSXcEIKXGR9etAz8klITjMi77KaqoEbxwDBPaQ2sib6u7VdhvUSMknknV/lrHAN+P4R7UyRT+5Q1IxMVPquCqJjeMW0vIurkLwrx6vaD6DQ1MYVYad/yNAGtuUxOGT4VKyt+WXtBgfXHvfug5yDFpmFRDZ7a8x6T3Rp8IU05tpAOMAbWjtvGAsDng90lyerw2wgAJPo/3rH98B6jb+JpCwezxxnSMjcKR1f//NXhj0Yu7wjfmCFF2ce22h50cmD65d+SmZ6jJUBHEb5s7Lv6DVu1B+f5xCswEJEkdIqRBr9j3pepYtvCNH1LR9KMvBUHOzf~4273217~3225649; permInfo=1718538146003869643A19102024A20102024; sbsd=sqHAGB30Ta0A8z/zMREw1QlBdUZJFVfS5ianDya3hGJj2kBywPtB0TWDtaopjSBnPeVjLqJ+h/060mf80IlwM4J9t+TwpOphBNW5zT9uZDdu+PJnU5upkluRfY/QN9b5YoHCv59domP+QVNenV72jhSH1pHHCXio2e0o8alRMgwIWYaf19It2lEKqbdvFQEEx; sbsd_o=0C9E8725A9422118CE6EF85A137D22C7D61C212569369D1DB118978CA6359BA4~sSLkgmnL1/0TJXjWPr64kxac1Lqj7GA+4VOPe7xTKzoHcriCbd37hz5XJ02cPsTG1jiyXVaXPyv8SXIsBE0eM7CMrbozZfbd/8lne2Gjl9Ht9Vgu1Tf3xb7osNSnc31TwNvlHtBXz4JSBIvHyA4qM8gVcocEWD/LEN91D5c9hHdFscQla85cFnQaE1E62LBU8LCAdSSIm/2mKiY78UZuOnnGvRatF7nLTobk7cqbj+bs=; sbsd_ss=ab8e18ef4e; webid=WlBFX05PX1NFU1NJT05fSVc=; webshop=bG9naW49MCZhbW91bnQ9MCZyZXNlcnZhdGlvbl9kdXJhdGlvbj0wJmtwc191cmw9JmFtb3VudF9ldmlkcz0wJnJhbmRvbT0zOTgwNTAmcmVmZXJlcj1odHRwcyUzQSUyRiUyRnd3dy56YXBwYS1jbHViLmNvLmlsJTJGJmtleT1xRGVKWHRmUGVjV1ombW9kPTEmZGV2aWNlX2luZm89MCZsYW5ndWFnZT1pdyZsb2NhbGU9aXdfaWwmY3VycmVuY3lfY29kZT1JTFM=; akavpau_eventim_production_short=1729435019~id=9251cd2f6224895a29698345a74d3514");
        builder.addHeader("priority", "u=0, i");
        builder.addHeader("referer", "https://www.zappa-club.co.il/artist/%D7%93%D7%A0%D7%99-%D7%A1%D7%A0%D7%93%D7%A8%D7%A1%D7%95%D7%9F/?affiliate=ZPE");
        builder.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"");
        builder.addHeader("sec-ch-ua-mobile", "?0");
        builder.addHeader("sec-ch-ua-platform", "\"Linux\"");
        builder.addHeader("sec-fetch-dest", "document");
        builder.addHeader("sec-fetch-mode", "navigate");
        builder.addHeader("sec-fetch-site", "same-origin");
        builder.addHeader("upgrade-insecure-requests", "1");
        builder.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36");
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Accept-Language", "he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7,lb;q=0.6");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Cookie", "__cmpiuid=3ee03cd71fc91111aef1c6166a89be9d");
        builder.addHeader("Referer", "https://www.zappa-club.co.il/");
        builder.addHeader("Sec-Fetch-Dest", "script");
        builder.addHeader("Sec-Fetch-Mode", "no-cors");
        builder.addHeader("Sec-Fetch-Site", "cross-site");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36");
        builder.addHeader("x-client-data", "CIa2yQEIorbJAQipncoBCOiOywEIkqHLAQjqmM0BCIegzQEIksbOAQinyM4BCLDIzgEImcrOAQ==");
        builder.addHeader("x-browser-channel", "stable");
        builder.addHeader("x-browser-copyright", "Copyright 2024 Google LLC. All rights reserved.");
        builder.addHeader("x-browser-validation", "vrdrOb79TZVKQF8a3V5ixRDvbQE=");
        builder.addHeader("x-browser-year", "2024");
        builder.addHeader("if-range", "\"1DEAE7DAB3E8E6B9FD860A201DA5B6ED\"");
        builder.addHeader("range", "bytes=330000-1037120");
        builder.addHeader("content-type", "text/plain;charset=UTF-8");
        builder.addHeader("origin", "https://www.zappa-club.co.il");
        Request request = builder
                .build();
        Response response = client.newCall(request).execute();
        try {
            return response.body().string();
        }
        finally {
            response.close();
        }
    }
}