package com.thistroll.service.troll.impl;

import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

/**
 * Unit test for {@link SpeechTypeResolverImpl}
 *
 * Created by MVW on 10/5/2017.
 */
public class SpeechTypeResolverImplTest {

    private SpeechTypeResolver speechTypeResolver = new SpeechTypeResolverImpl(false);

    @Test
    public void testOpenEndedQuestions() throws Exception {
        assertOpenEnded("how are you");
        assertOpenEnded("how are you?");
        assertOpenEnded("what's your favorite color");
        assertOpenEnded("who should i blame?");
        assertOpenEnded("when did the $ run out");
        assertOpenEnded("to whom am I speaking?");
        assertOpenEnded("whom am I speaking to?");
        assertOpenEnded("where should I put the plates");
        assertOpenEnded("which condoms should I buy");
        assertOpenEnded("whose baby is it?");
        assertOpenEnded("why should I care");
    }

    @Test
    public void testYesNoQuestions() throws Exception {
        assertYesNo("buildings are made of wood?");
        assertYesNo("are my things fat");
        assertYesNo("can you massage my fat thighs?");
        assertYesNo("could I possibly be lamer");
        assertYesNo("did I say that out loud?");
        assertYesNo("does orange really rhyme with anything");
        assertYesNo("is it all over?");
        assertYesNo("ought I backtrack");
        assertYesNo("should somebody call 911?");
        assertYesNo("was I too harsh?");
        assertYesNo("will you be there monday?");
        assertYesNo("would somebody please tell me what the hell is going on");
    }

    @Test
    public void testStatements() throws Exception {
        assertStatement("I used to play baseball");
        assertStatement("I know why you did it");
        assertStatement("I know why you did it");
    }

    @Test
    public void testConditionalStatements() throws Exception {
        assertOpenEnded("if we destroy the earth where will we move?");
        assertYesNo("if i die will you cremate me");
        assertStatement("if i've told you once i've told you a thousand times'");
    }

    @Test
    public void testPrepositionalClauses() throws Exception {
        assertOpenEnded("after this job what should I do next");
        assertYesNo("before I told you did you already know");
        assertStatement("except for yesterday I have not had a drink in forty days");
    }

    @Test
    @Ignore
    // TODO: support for these failing test cases
    public void testFailingConditions() throws Exception {
        assertStatement("if i were you i'd make a big scene about it");
        assertStatement("after tax season I will be super happy");
        assertStatement("during the days it was I stayed home");
        assertStatement("without her I don't know what I'd do");

        assertOpenEnded("tell me, how old are you?");
        assertOpenEnded("tell me, how old are you");
        assertOpenEnded("i forget, how old are you");
        assertOpenEnded("bummer. what are you doing for christmas?");
        assertOpenEnded("bummer. what are you doing for christmas");

        assertOpenEnded("were the legislature to vote for impeachment, what would Trump do?");
        assertOpenEnded("suppose the legislature were to vote for impeachment - what would Trump do?");

        assertStatement("why on earth I tried, I have no idea");

        assertStatement("when in doubt, drink");

        assertStatement("what a mess");

        assertStatement("how he did it, I have no idea");

        assertStatement("as to who called, I can't say");
    }

    private void assertOpenEnded(String speech) {
        assertThat(speechTypeResolver.resolve(speech), is(SpeechType.OPEN_ENDED_QUESTION));
    }

    private void assertYesNo(String speech) {
        assertThat(speechTypeResolver.resolve(speech), is(SpeechType.YES_NO_QUESTION));
    }

    private void assertStatement(String speech) {
        assertThat(speechTypeResolver.resolve(speech), is(SpeechType.STATEMENT));
    }
}