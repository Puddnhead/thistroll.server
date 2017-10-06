package com.thistroll.service.troll.impl;

import com.google.common.collect.Sets;
import com.thistroll.service.troll.api.SpeechNormalizationService;
import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

/**
 * Implementation for {@link SpeechNormalizationService}
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechNormalizationServiceImpl implements SpeechNormalizationService {

    private SpeechTypeResolver speechTypeResolver;

    /**
     * Punctuation-stripping occurs after toLowerCase(), so no need to worry about uppercase letters
     */
    static Set<Character> RECOGNIZED_CHARACTERS = Sets.newHashSet(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' ,'n', 'o', 'p', 'q', 'r', 's', 't',
                't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '-', '?', ' '
            );
    
    @Override
    public String normalize(String speech) {
        String normalized = speech.toLowerCase();
        normalized = removeContractions(normalized);
        normalized = replaceKnownSlang(normalized);
        normalized = normalizeWhiteSpace(normalized);
        normalized = stripPunctuation(normalized);
        normalized = appendQuestionMarkIfNecessary(normalized);

        return normalized;
    }

    private String removeContractions(String text) {
        // TODO: better normalization of all the nots - 'are not the beasleys' should be 'are the beasleys not'
        String normalized = text.replaceAll("aren't", "are not");
        normalized = normalized.replaceAll("can't", "cannot");
        normalized = normalized.replaceAll("couldn't", "could not");
        normalized = normalized.replaceAll("couldnt", "could not");
        normalized = normalized.replaceAll("didn't", "did not");
        normalized = normalized.replaceAll("didnt", "did not");
        normalized = normalized.replaceAll("doesn't", "does not");
        normalized = normalized.replaceAll("doesnt", "does not");
        normalized = normalized.replaceAll("hadn't", "had not");
        normalized = normalized.replaceAll("haven't", "have not");
        normalized = normalized.replaceAll("he's", "he is");
        normalized = normalized.replaceAll("i'm", "i am");
        normalized = normalized.replaceAll("isn't", "is not");
        normalized = normalized.replaceAll("isnt", "is not");
        normalized = normalized.replaceAll("she's", "she is");
        normalized = normalized.replaceAll("shouldn't", "should not");
        normalized = normalized.replaceAll("shouldnt", "should not");
        normalized = normalized.replaceAll("they're", "they are");
        normalized = normalized.replaceAll("theyre", "they are");
        normalized = normalized.replaceAll("wasn't", "was not");
        normalized = normalized.replaceAll("wasnt", "was not");
        normalized = normalized.replaceAll("we're", "we are");
        normalized = normalized.replaceAll("werent", "were not");
        normalized = normalized.replaceAll("weren't", "were not");
        normalized = normalized.replaceAll("what've", "what have");
        normalized = normalized.replaceAll("won't", "will not");
        normalized = normalized.replaceAll("wouldn't", "would not");
        normalized = normalized.replaceAll("wouldnt", "would not");
        normalized = normalized.replaceAll("you're", "you are");
        
        return normalized;
    }
    
    private String replaceKnownSlang(String text) {
        String normalized = text.replaceAll("coulda", "could have");
        normalized = normalized.replaceAll("gonna", "going to");
        normalized = normalized.replaceAll("shoulda", "should have");
        normalized = normalized.replaceAll("spose ", "suppose ");
        normalized = normalized.replaceAll("woulda", "would have");
        
        return normalized;
    }

    private String normalizeWhiteSpace(String text) {
        return text.replaceAll("[\t\r\n]", " ");
    }
    
    private String stripPunctuation(String text) {
        String normalized = "";
        for (int i = 0; i < text.length(); i++) {
            Character ch = text.charAt(i);
            if (RECOGNIZED_CHARACTERS.contains(ch)) {
                normalized += ch;
            }
        }
        
        return normalized;
    }

    private String appendQuestionMarkIfNecessary(String text) {
        if (!speechTypeResolver.resolve(text).equals(SpeechType.STATEMENT) &&
                text.charAt(text.length() - 1) != '?') {
            return text + '?';
        }

        return text;
    }

    @Required
    public void setSpeechTypeResolver(SpeechTypeResolver speechTypeResolver) {
        this.speechTypeResolver = speechTypeResolver;
    }
}
