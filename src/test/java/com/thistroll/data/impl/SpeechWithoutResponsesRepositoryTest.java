package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.Speech;
import com.thistroll.exceptions.ValidationException;
import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link SpeechWithoutResponsesRepository}
 * Not strictly a unit test as it tests logic in the mapper class and base class
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechWithoutResponsesRepositoryTest extends AbstractRepositoryTest {

    @InjectMocks
    SpeechWithoutResponsesRepository repository;

    private static final DateTime NOW = new DateTime();
    private static final String ID = "id";
    private static final String TEXT = "what is the deal?";

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        repository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Test
    public void testSaveReturnsNewInstance() throws Exception {
        Speech original = new Speech.Builder()
                .text(TEXT)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        repository.saveSpeech(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(Speech.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(Speech.TEXT_PROPERTY), is(TEXT));
        assertThat(item.getLong(Speech.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(Speech.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test(expected = ValidationException.class)
    public void testSaveFailsForMissingText() throws Exception {
        Speech speech = new Speech.Builder()
                .id(ID)
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

    @Test(expected = NotImplementedException.class)
    public void testUpdateThrowsException() throws Exception {
        repository.updateSpeech(mock(Speech.class));
    }

    private Item createItem() {
        return new Item()
                .withString(Speech.ID_PROPERTY, ID)
                .withString(Speech.TEXT_PROPERTY, TEXT)
                .withNumber(Speech.CREATED_ON_PROPERTY, NOW.getMillis())
                .withNumber(Speech.LAST_UPDATED_ON_PROPERTY, NOW.getMillis());
    }

    private void assertCommonFields(Speech speech) {
        assertThat(speech.getId(), is(ID));
        assertThat(speech.getText(), is(TEXT));
        assertThat(speech.getCreatedOn(), is(NOW));
        assertThat(speech.getLastUpdatedOn(), is(NOW));
    }
}