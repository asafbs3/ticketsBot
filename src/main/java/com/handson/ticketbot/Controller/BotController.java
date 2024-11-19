package com.handson.ticketbot.Controller;

import com.handson.ticketbot.Service.BarbyService;
import com.handson.ticketbot.Service.EventerService;
import com.handson.ticketbot.Service.GreyService;
import com.handson.ticketbot.Service.ZappaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bot")
public class BotController {

    @Autowired
    ZappaService zappaService;

    @Autowired
    BarbyService barbyService;

    @Autowired
    GreyService greyService;

    @Autowired
    EventerService eventerService;

    @RequestMapping(value="/barby", method = RequestMethod.GET)
    public ResponseEntity<?> getBarbyProduct(@RequestParam String keyword) throws IOException {
        return new ResponseEntity<>(barbyService.searchProduct(keyword), HttpStatus.OK);
    }

    @RequestMapping(value="/grey", method = RequestMethod.GET)
    public ResponseEntity<?> getGreyProduct(@RequestParam String keyword) throws IOException {
        return new ResponseEntity<>(greyService.searchProducts(keyword), HttpStatus.OK);
    }

    @RequestMapping(value="/eventer", method = RequestMethod.GET)
    public ResponseEntity<?> getEventerProduct(@RequestParam String keyword) throws IOException {
        return new ResponseEntity<>(eventerService.searchProduct(keyword), HttpStatus.OK);
    }

    @RequestMapping(value = "/searchAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProduct(@RequestParam String keyword) throws IOException {
        Map<String, Object> allProducts = new HashMap<>();

        Object barbyResults = barbyService.searchProduct(keyword);
        if(barbyResults != null)
            allProducts.put("barby", barbyResults);
        Object greyResults = greyService.searchProducts(keyword);
        if(greyResults != null)
            allProducts.put("grey", greyResults);
        Object eventerResults = eventerService.searchProduct(keyword);
        if(eventerResults != null)
            allProducts.put("eventer", eventerResults);

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = { RequestMethod.POST})
    public ResponseEntity<?> getBotResponse(@RequestBody BotQuery query) throws IOException {
        HashMap<String, String> params = query.getQueryResult().getParameters();
        String res = "Not found";
        if (params.containsKey("artist")) {
            res = searchAll(params.get("artist"));
        }
        return new ResponseEntity<>(BotResponse.of(res), HttpStatus.OK);
    }

    public String searchAll(String keyword) throws IOException {
        String ret = "";
        ret += barbyService.searchProduct(keyword);
        ret += greyService.searchProducts(keyword);
        ret += eventerService.searchProduct(keyword);

        if(ret.isEmpty())
            return "Sorry, I couldn't find any concerts for this artist";

        return ret;
    }

    static class BotQuery {
        QueryResult queryResult;

        public QueryResult getQueryResult() {
            return queryResult;
        }
    }

    static class QueryResult {
        HashMap<String, String> parameters;
        public HashMap<String, String> getParameters() {
            return parameters;
        }
    }

    static class BotResponse {
        String fulfillmentText;
        String source = "BOT";

        public String getFulfillmentText() {
            return fulfillmentText;
        }

        public String getSource() {
            return source;
        }

        public static BotResponse of(String fulfillmentText) {
            BotResponse res = new BotResponse();
            res.fulfillmentText = fulfillmentText;
            return res;
        }
    }
}
