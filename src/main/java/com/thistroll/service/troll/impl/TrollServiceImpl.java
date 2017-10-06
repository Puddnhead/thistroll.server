package com.thistroll.service.troll.impl;

import com.thistroll.exceptions.UnsupportedSpeechException;
import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import com.thistroll.service.troll.repositories.RandomResponseRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation class for {@link com.thistroll.service.client.TrollService}
 *
 * Created by MVW on 7/21/2017.
 */
public class TrollServiceImpl implements com.thistroll.service.client.TrollService {

    private static final Map<String, String> HARDCODED_ANSWERS;

    private static final DateTime BIRTHDATE = new DateTime(2017, 7, 21, 0, 0);

    private SpeechTypeResolver speechTypeResolver;

    private RandomResponseRepository statementRandomResponseRepository;
    private RandomResponseRepository openEndedQuestionRandomResponseRepository;
    private RandomResponseRepository yesNoQuestionRandomResponseRepository;

    static {
        HARDCODED_ANSWERS = new HashMap<>();
        HARDCODED_ANSWERS.put("WHAT'S YOUR NAME", "I'm Pan! Mucho gusto.");
        HARDCODED_ANSWERS.put("WHAT'S THE CAPITAL OF UKRAINE", "Kiev of course!");
    }

    @Override
    public String trollSpeak(String statement) {
        HARDCODED_ANSWERS.put("HOW OLD ARE YOU", calculateAge());

        String normalized = normalizeSpeech(statement);
        String response = HARDCODED_ANSWERS.get(normalized);

        if (response == null) {
            SpeechType speechType = speechTypeResolver.resolve(statement);
            switch (speechType) {
                case OPEN_ENDED_QUESTION:
                    response = openEndedQuestionRandomResponseRepository.getRandomResponse();
                    break;
                case YES_NO_QUESTION:
                    response = yesNoQuestionRandomResponseRepository.getRandomResponse();
                    break;
                default:
                    response = statementRandomResponseRepository.getRandomResponse();
            }
        }

        return response;
    }

    private String normalizeSpeech(String statement) {
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

        return normalized;
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

    @Required
    public void setSpeechTypeResolver(SpeechTypeResolver speechTypeResolver) {
        this.speechTypeResolver = speechTypeResolver;
    }

    @Required
    public void setStatementRandomResponseRepository(RandomResponseRepository statementRandomResponseRepository) {
        this.statementRandomResponseRepository = statementRandomResponseRepository;
    }

    @Required
    public void setOpenEndedQuestionRandomResponseRepository(RandomResponseRepository openEndedQuestionRandomResponseRepository) {
        this.openEndedQuestionRandomResponseRepository = openEndedQuestionRandomResponseRepository;
    }

    @Required
    public void setYesNoQuestionRandomResponseRepository(RandomResponseRepository yesNoQuestionRandomResponseRepository) {
        this.yesNoQuestionRandomResponseRepository = yesNoQuestionRandomResponseRepository;
    }
}
