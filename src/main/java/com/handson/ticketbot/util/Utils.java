package com.handson.ticketbot.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class Utils {
    public static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }
}
