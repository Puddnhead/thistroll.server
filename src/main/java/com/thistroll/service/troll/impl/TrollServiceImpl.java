package com.thistroll.service.troll.impl;

import com.thistroll.data.api.SpeechRepository;
import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.SpeechNotFoundException;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.server.logging.ThrowsWarning;
import com.thistroll.service.troll.api.SpeechNormalizationService;
import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import com.thistroll.service.troll.repositories.RandomResponseRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation class for {@link com.thistroll.service.client.TrollService}
 *
 * Created by MVW on 7/21/2017.
 */
public class TrollServiceImpl implements com.thistroll.service.client.TrollService {

    private static final Map<String, String> HARDCODED_ANSWERS;

    // The troll service was born on July 21st, 2017
    private static final DateTime BIRTHDATE = new DateTime(2017, 7, 21, 0, 0);

    private SpeechTypeResolver speechTypeResolver;
    private SpeechNormalizationService speechNormalizationService;

    private RandomResponseRepository statementRandomResponseRepository;
    private RandomResponseRepository openEndedQuestionRandomResponseRepository;
    private RandomResponseRepository yesNoQuestionRandomResponseRepository;

    private SpeechRepository knownSpeechRepository;
    private SpeechRepository speechWithoutResponsesRepository;

    private int maximumSpeechLength;

    static {
        HARDCODED_ANSWERS = new HashMap<>();
        HARDCODED_ANSWERS.put("what is your name?", "I'm Pan! Pleasure to meet you.");
        HARDCODED_ANSWERS.put("what is the capital of ukraine?", "Kiev of course!");
    }

    @ThrowsError
    @Override
    public String trollSpeak(String text) {
        HARDCODED_ANSWERS.put("how old are you?", calculateAge());

        String normalized = speechNormalizationService.normalize(text);
        String response = HARDCODED_ANSWERS.get(normalized);

        if (response == null) {
            Speech speech = knownSpeechRepository.getSpeechByText(normalized);
            if (speech != null) {
                response = speech.getRandomResponse();
            }
        }

        if (response == null) {
            Speech speech = speechWithoutResponsesRepository.getSpeechByText(normalized);
            if (speech == null && normalized.length() < maximumSpeechLength) {
                speechWithoutResponsesRepository.saveSpeech(new Speech.Builder().text(normalized).build());
            }
            response = getRandomResponse(normalized);
        }

        return response;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsWarning
    @Override
    public Speech getSpeechByText(String text) {
        String normalized = speechNormalizationService.normalize(text);
        Speech speech = knownSpeechRepository.getSpeechByText(normalized);
        if (speech == null) {
            speech = speechWithoutResponsesRepository.getSpeechByText(normalized);
        }

        if (speech == null) {
            throw new SpeechNotFoundException("No speech with the given text");
        }

        return speech;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Outcome deleteSpeech(String idOrText) {
        Outcome outcome = Outcome.FAILURE;
        String normalized = speechNormalizationService.normalize(idOrText);

        Speech speech = knownSpeechRepository.getSpeechById(idOrText);
        if (speech == null) {
            speech = knownSpeechRepository.getSpeechByText(normalized);
        }
        if (speech != null) {
            knownSpeechRepository.deleteSpeech(speech.getId());
            outcome = Outcome.SUCCESS;
        }

        speech = speechWithoutResponsesRepository.getSpeechById(idOrText);
        if (speech == null) {
            speech = speechWithoutResponsesRepository.getSpeechByText(normalized);
        }
        if (speech != null) {
            speechWithoutResponsesRepository.deleteSpeech(speech.getId());
            outcome = Outcome.SUCCESS;
        }

        return outcome;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Speech getNextSpeechWithNoResponse() {
        return speechWithoutResponsesRepository.getNextSpeech();
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Speech updateResponses(Speech speech) {
        Speech updated;

        Speech speechWithoutResponses = speech.getId() != null
                ? speechWithoutResponsesRepository.getSpeechById(speech.getId())
                : speechWithoutResponsesRepository.getSpeechByText(speech.getText());

        if (speechWithoutResponses != null) {
            speechWithoutResponsesRepository.deleteSpeech(speechWithoutResponses.getId());
        }

        Speech knownSpeech = speech.getId() != null
                ? knownSpeechRepository.getSpeechById(speech.getId())
                : knownSpeechRepository.getSpeechByText(speech.getText());

        if (knownSpeech != null) {
            updated = knownSpeechRepository.updateSpeech(speech);
        } else {
            updated = knownSpeechRepository.saveSpeech(speech);
        }

        return updated;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public int getSpeechWithoutResponsesCount() {
        return speechWithoutResponsesRepository.getTotalNumberOfSpeeches();
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public int getKnownSpeechCount() {
        return knownSpeechRepository.getTotalNumberOfSpeeches();
    }

    private String getRandomResponse(String speech) {
        String response;

        SpeechType speechType = speechTypeResolver.resolve(speech);
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

    @Required
    public void setSpeechTypeResolver(SpeechTypeResolver speechTypeResolver) {
        this.speechTypeResolver = speechTypeResolver;
    }

    @Required
    public void setSpeechNormalizationService(SpeechNormalizationService speechNormalizationService) {
        this.speechNormalizationService = speechNormalizationService;
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

    @Required
    public void setKnownSpeechRepository(SpeechRepository knownSpeechRepository) {
        this.knownSpeechRepository = knownSpeechRepository;
    }

    @Required
    public void setSpeechWithoutResponsesRepository(SpeechRepository speechWithoutResponsesRepository) {
        this.speechWithoutResponsesRepository = speechWithoutResponsesRepository;
    }

    @Required
    public void setMaximumSpeechLength(int maximumSpeechLength) {
        this.maximumSpeechLength = maximumSpeechLength;
    }
}
