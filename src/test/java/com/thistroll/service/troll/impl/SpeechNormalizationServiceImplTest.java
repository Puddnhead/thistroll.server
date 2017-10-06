package com.thistroll.service.troll.impl;

import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link SpeechNormalizationServiceImpl}
 *
 * Created by MVW on 10/6/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpeechNormalizationServiceImplTest {

    @InjectMocks
    private SpeechNormalizationServiceImpl speechNormalizationService;

    @Mock
    private SpeechTypeResolver speechTypeResolver;

    @Before
    public void setup() throws Exception {
        when(speechTypeResolver.resolve(anyString())).thenReturn(SpeechType.STATEMENT);
    }

    @Test
    public void testLowerCaseNormalization() throws Exception {
        assertThat(speechNormalizationService.normalize("I love Mr Rogers"), is("i love mr rogers"));
    }

    @Test
    public void testContractionsNormalization() throws Exception {
        assertThat(speechNormalizationService.normalize("i can't and i won't"), is("i cannot and i will not"));
        assertThat(speechNormalizationService.normalize("i hadn't thought"), is("i had not thought"));
        assertThat(speechNormalizationService.normalize("i didn't know"), is("i did not know"));
        assertThat(speechNormalizationService.normalize("she's incredible"), is("she is incredible"));
        assertThat(speechNormalizationService.normalize("he's an ass"), is("he is an ass"));
        assertThat(speechNormalizationService.normalize("weren't you done"), is("were not you done"));
        assertThat(speechNormalizationService.normalize("haven't we met before"), is("have not we met before"));
        assertThat(speechNormalizationService.normalize("isn't that special"), is("is not that special"));
        assertThat(speechNormalizationService.normalize("wouldn't you like to know"), is("would not you like to know"));
        assertThat(speechNormalizationService.normalize("they're insufferable"), is("they are insufferable"));
        assertThat(speechNormalizationService.normalize("we're the best"), is("we are the best"));
        assertThat(speechNormalizationService.normalize("you're the best"), is("you are the best"));
        assertThat(speechNormalizationService.normalize("i'm the best"), is("i am the best"));
    }

    @Test
    public void testSlangNormalization() throws Exception {
        assertThat(speechNormalizationService.normalize("i am gonna marry you"), is("i am going to marry you"));
        assertThat(speechNormalizationService.normalize("you shoulda waited"), is("you should have waited"));
        assertThat(speechNormalizationService.normalize("i woulda waited"), is("i would have waited"));
        assertThat(speechNormalizationService.normalize("i spose we coulda waited"), is("i suppose we could have waited"));
    }

    @Test
    public void testWhiteSpaceNormalization() throws Exception {
        assertThat(speechNormalizationService.normalize("a\tb\rc\nd e"), is("a b c d e"));
    }

    @Test
    public void testPunctuationStrippingNormalization() throws Exception {
        assertThat(speechNormalizationService.normalize("i know right!"), is("i know right"));
        assertThat(speechNormalizationService.normalize("if i were king, i would eat a lot"), is("if i were king i would eat a lot"));
        assertThat(speechNormalizationService.normalize("u.s.a."), is("usa"));
    }

    @Test
    public void testQuestionMarkNormalization() {
        assertThat(speechNormalizationService.normalize("i hate you"), is("i hate you"));

        when(speechTypeResolver.resolve(anyString())).thenReturn(SpeechType.OPEN_ENDED_QUESTION);
        assertThat(speechNormalizationService.normalize("do you hate me"), is("do you hate me?"));
    }
}