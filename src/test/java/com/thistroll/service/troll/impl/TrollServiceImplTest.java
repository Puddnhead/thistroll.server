package com.thistroll.service.troll.impl;

import com.thistroll.data.api.SpeechRepository;
import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.troll.api.SpeechNormalizationService;
import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import com.thistroll.service.troll.repositories.RandomResponseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link TrollServiceImpl}
 *
 * Created by MVW on 10/6/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrollServiceImplTest {

    @InjectMocks
    private TrollServiceImpl trollService;

    @Mock
    private SpeechTypeResolver speechTypeResolver;

    @Mock
    private SpeechNormalizationService speechNormalizationService;

    private RandomResponseRepository randomResponseRepository = mock(RandomResponseRepository.class);

    @Captor
    private ArgumentCaptor<Speech> speechCaptor;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    private SpeechRepository knownSpeechRepository = mock(SpeechRepository.class);
    private SpeechRepository speechWithoutResponsesRepository = mock(SpeechRepository.class);

    private static final String RANDOM_RESPONSE = "oh wow";
    private static final String KNOWN_RESPONSE = "I've heard that.";
    private static final String RANDOM_TEXT = "some random text";
    private static final String ID = "some-id";

    @Before
    public void setup() throws Exception {
        trollService.setMaximumSpeechLength(500);
        trollService.setKnownSpeechRepository(knownSpeechRepository);
        trollService.setSpeechWithoutResponsesRepository(speechWithoutResponsesRepository);
        trollService.setStatementRandomResponseRepository(randomResponseRepository);
        trollService.setOpenEndedQuestionRandomResponseRepository(randomResponseRepository);
        trollService.setYesNoQuestionRandomResponseRepository(randomResponseRepository);
        when(randomResponseRepository.getRandomResponse()).thenReturn(RANDOM_RESPONSE);
        when(speechTypeResolver.resolve(anyString())).thenReturn(SpeechType.STATEMENT);
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(speechNormalizationService).normalize(anyString());
    }

    @Test
    public void testSpeakSavesNewSpeechToSpeechWithoutResponsesRepository() throws Exception {
        when(speechWithoutResponsesRepository.saveSpeech(speechCaptor.capture())).thenReturn(mock(Speech.class));
        trollService.trollSpeak(RANDOM_TEXT);
        assertThat(speechCaptor.getValue().getText(), is(RANDOM_TEXT));
    }

    @Test
    public void testSpeakFetchesFromHardcodedResponsesFirst() throws Exception {
        String response = trollService.trollSpeak("what is your name?");
        assertThat(response, is("I'm Pan! Pleasure to meet you."));
    }

    @Test
    public void testSpeakFetchesFromKnownSpeechSecond() throws Exception {
        Speech speech = new Speech.Builder()
                .responses(Collections.singletonList(KNOWN_RESPONSE))
                .build();
        when(knownSpeechRepository.getSpeechByText(RANDOM_TEXT)).thenReturn(speech);
        String result = trollService.trollSpeak(RANDOM_TEXT);
        assertThat(result, is(KNOWN_RESPONSE));
    }

    @Test
    public void testSpeakFetchesFromRandomRepositoriesLast() throws Exception {
        String result = trollService.trollSpeak(RANDOM_TEXT);
        assertThat(result, is(RANDOM_RESPONSE));
    }

    @Test
    public void testUpdateDeletesFromSpeechWithoutResponsesRepositoryIfNecessary() throws Exception {
        Speech speech = createUpdateRequest();
        when(speechWithoutResponsesRepository.getSpeechById(ID)).thenReturn(speech);
        when(speechWithoutResponsesRepository.deleteSpeech(idCaptor.capture())).thenReturn(Outcome.SUCCESS);
        trollService.updateResponses(speech);
        assertThat(idCaptor.getValue(), is(ID));
    }

    @Test
    public void testUpdateUpdatesExistingObjectInKnownSpeechRepositoryIfNotNew() throws Exception {
        Speech request = createUpdateRequest();
        when(knownSpeechRepository.getSpeechById(ID)).thenReturn(request);
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(knownSpeechRepository).updateSpeech(any(Speech.class));
        Speech outcome = trollService.updateResponses(request);

        assertThat(outcome.getId(), is(ID));
        assertThat(outcome.getResponses().get(0), is(RANDOM_RESPONSE));
    }

    @Test
    public void testUpdateSavesNewObjectIfNew() throws Exception {
        Speech request = createUpdateRequest();
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(knownSpeechRepository).saveSpeech(any(Speech.class));
        Speech outcome = trollService.updateResponses(request);

        assertThat(outcome.getId(), is(ID));
        assertThat(outcome.getResponses().get(0), is(RANDOM_RESPONSE));
    }

    @Test
    public void testSpeakDoesntSaveSpeechLargerThanMaximumLength() throws Exception {
        trollService.setMaximumSpeechLength(1);
        when(speechWithoutResponsesRepository.saveSpeech(speechCaptor.capture())).thenReturn(mock(Speech.class));
        trollService.trollSpeak(RANDOM_TEXT);
        verify(speechWithoutResponsesRepository, never()).saveSpeech(any(Speech.class));
    }

    private Speech createUpdateRequest() {
        return new Speech.Builder()
                .id(ID)
                .responses(Collections.singletonList(RANDOM_RESPONSE))
                .build();
    }
}