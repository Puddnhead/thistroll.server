package com.thistroll.troll;

import com.thistroll.service.client.TrollService;
import com.thistroll.exceptions.UnsupportedSpeechException;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MVW on 7/21/2017.
 */
public class HardcodedTrollService implements TrollService {

    private static final Map<String, String> HARDCODED_ANSWERS;
    private static final List<String> CONFUSED_RESPONSES;

    private static final DateTime BIRTHDATE = new DateTime(2017, 7, 21, 0, 0);

    static {
        HARDCODED_ANSWERS = new HashMap<>();
        HARDCODED_ANSWERS.put("WHAT'S YOUR NAME", "I'm Pan! Mucho gusto.");
        HARDCODED_ANSWERS.put("WHAT'S THE CAPITAL OF UKRAINE", "Kiev of course!");

        CONFUSED_RESPONSES = Arrays.asList(
                "I like it when people talk to me! But I'm not too smart. Sorry, I can't help you.",
                "I'm only an infant and I don't know a lot. But thanks for asking!",
                "Quién supa?",
                "Got me.",
                "That's a tough one.",
                "Er...",
                "Wish I could help you.",
                "How should I know!?",
                "They never tell me anything",
                "I forgot?",
                "If I had a nickel for every time somebody asked me that one...",
                "I'm not telling!",
                "I have no idea.",
                "43?",
                "Let me get back to you...",
                "I have to go now...",
                "Ask me something else.",
                "I basically only know the capital of Kiev. Don't ask me why.",
                "Hrm, well. Ahem.",
                "Try Google?"
        );
    }

    @Override
    public String trollSpeak(String statement) {
        HARDCODED_ANSWERS.put("HOW OLD ARE YOU", calculateAge());

        String normalized;
        try {
            normalized = URLDecoder.decode(statement, "UTF-8")
                    .replaceAll("\\?", "")
                    .toUpperCase()
                    .replaceAll("WHAT IS", "WHAT'S")
                    .trim();
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedSpeechException("Could not decode speech");
        }
        String response = HARDCODED_ANSWERS.get(normalized);

        if (response == null) {
            response = CONFUSED_RESPONSES.get((int)(Math.random() * CONFUSED_RESPONSES.size()));
        }

        return response;
    }

    private static String calculateAge() {
        DateTime now = new DateTime();
        boolean carry = now.getMonthOfYear() < BIRTHDATE.getMonthOfYear();
        int months = carry ? 12 - (BIRTHDATE.getMonthOfYear() - now.getMonthOfYear())
                : now.getMonthOfYear() - BIRTHDATE.getMonthOfYear();
        int years = carry ? now.getYear() - BIRTHDATE.getYear() - 1 : now.getYear() - BIRTHDATE.getYear();

        if (months == 0 && years == 0) {
            return "I'm less than one month old. So not too old.";
        }

        String yearsPhrase = years == 0 ? "" : years + " years and ";
        String monthsPhrase = months == 1 ? "1 month old." : months + " months old.";
        return "I'm " + yearsPhrase + monthsPhrase;
    }
}
