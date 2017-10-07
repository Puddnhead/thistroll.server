package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.Speech;
import com.thistroll.exceptions.SpeechNotFoundException;
import com.thistroll.exceptions.ValidationException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link KnownSpeechRepository}
 * Not strictly a unit test as it tests the mapper class as well
 *
 * Created by MVW on 10/6/2017.
 */
public class KnownSpeechRepositoryTest extends AbstractRepositoryTest{

    @InjectMocks
    private KnownSpeechRepository repository;

    private static final DateTime NOW = new DateTime();
    private static final String ID = "id";
    private static final String TEXT = "what is the deal?";
    private static final String RESPONSE_1 = "No deal!";
    private static final String RESPONSE_2 = "You blew it.";
    private static final List<String> RESPONSES = Arrays.asList(RESPONSE_1, RESPONSE_2);

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        repository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Test
    public void testSaveReturnsNewInstance() throws Exception {
        Speech original = new Speech.Builder()
                .text(TEXT)
                .responses(RESPONSES)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        repository.saveSpeech(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(Speech.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(Speech.TEXT_PROPERTY), is(TEXT));
        assertThat(item.getList(Speech.RESPONSES_PROPERTY), is(RESPONSES));
        assertThat(item.getLong(Speech.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(Speech.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test(expected = ValidationException.class)
    public void testSaveFailsForMissingText() throws Exception {
        Speech speech = new Speech.Builder()
                .responses(RESPONSES)
                .build();
        repository.saveSpeech(speech);
    }

    @Test(expected = ValidationException.class)
    public void testSaveFailsForMissingResponses() throws Exception {
        Speech speech = new Speech.Builder()
                .text(TEXT)
                .build();
        repository.saveSpeech(speech);
    }

    @Test
    public void testFindById() throws Exception {
        Item item = createItem();
        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Speech speech = repository.getSpeechById(ID);
        assertCommonFields(speech);
    }

    @Test
    public void testFindByText() throws Exception {
        Item item = createItem();
        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Speech speech = repository.getSpeechByText(TEXT);
        assertCommonFields(speech);
    }

    @Test
    public void testUpdate() throws Exception {
        Item item = createItem();
        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Speech speech = new Speech.Builder()
                .id(ID)
                .responses(RESPONSES)
                .build();
        Speech outcome = repository.updateSpeech(speech);
        assertCommonFields(outcome);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateFailsForMissingId() throws Exception {
        Speech speech = new Speech.Builder()
                .responses(RESPONSES)
                .build();
        repository.updateSpeech(speech);
    }

    @Test(expected = SpeechNotFoundException.class)
    public void testUpdateFailsForMissingSpeech() throws Exception {
        Speech speech = new Speech.Builder()
                .id(ID)
                .responses(RESPONSES)
                .build();
        repository.updateSpeech(speech);
    }

    private Item createItem() {
        return new Item()
                .withString(Speech.ID_PROPERTY, ID)
                .withString(Speech.TEXT_PROPERTY, TEXT)
                .withList(Speech.RESPONSES_PROPERTY, RESPONSES)
                .withNumber(Speech.CREATED_ON_PROPERTY, NOW.getMillis())
                .withNumber(Speech.LAST_UPDATED_ON_PROPERTY, NOW.getMillis());
    }

    private void assertCommonFields(Speech speech) {
        assertThat(speech.getId(), is(ID));
        assertThat(speech.getText(), is(TEXT));
        assertThat(speech.getResponses(), is(RESPONSES));
        assertThat(speech.getCreatedOn(), is(NOW));
        assertThat(speech.getLastUpdatedOn(), is(NOW));
    }
}