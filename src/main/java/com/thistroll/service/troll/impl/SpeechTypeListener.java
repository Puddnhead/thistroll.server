package com.thistroll.service.troll.impl;

import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.grammar.SpeechBaseListener;
import com.thistroll.service.troll.grammar.SpeechParser;

/**
 * Listener class for parsing a grammar tree to determine a speech type
 *
 * Created by MVW on 10/5/2017.
 */
public class SpeechTypeListener extends SpeechBaseListener {

    private SpeechType speechType = SpeechType.STATEMENT;

    @Override
    public void enterOpen_ended_question(SpeechParser.Open_ended_questionContext ctx) {
        this.speechType = SpeechType.OPEN_ENDED_QUESTION;
    }

    @Override
    public void enterYes_no_question(SpeechParser.Yes_no_questionContext ctx) {
        this.speechType = SpeechType.YES_NO_QUESTION;
    }

    public SpeechType getSpeechType() {
        return speechType;
    }
}
