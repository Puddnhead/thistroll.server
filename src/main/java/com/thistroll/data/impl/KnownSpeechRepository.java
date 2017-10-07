package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.thistroll.data.api.SpeechRepository;
import com.thistroll.domain.Speech;
import com.thistroll.exceptions.SpeechNotFoundException;
import com.thistroll.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * A repository for speeches with responses that can be updated
 *
 * Created by MVW on 10/6/2017.
 */
public class KnownSpeechRepository extends SpeechRepositoryBase implements SpeechRepository {

    public static final String TABLE_NAME = "known_speech";

    @Override
    public Speech saveSpeech(Speech speech) {
        validateResponses(speech);
        return super.saveSpeech(speech);
    }

    @Override
    public Speech updateSpeech(Speech speech) {
        validateResponses(speech);

        String id = speech.getId();
        if (StringUtils.isEmpty(id)) {
            String text = speech.getText();
            if (StringUtils.isEmpty(text)) {
                throw new ValidationException("Update request must include 'id' or 'text'");
            }
            id = hashText(text);
        }
        validateSpeechExists(id);

        Table table = getTable();

        List<AttributeUpdate> attributeUpdates = new ArrayList<>();
        attributeUpdates.add(new AttributeUpdate(Speech.LAST_UPDATED_ON_PROPERTY).put(new DateTime().getMillis()));
        attributeUpdates.add(new AttributeUpdate(Speech.RESPONSES_PROPERTY).put(speech.getResponses()));

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(Speech.ID_PROPERTY, id)
                .withAttributeUpdate(attributeUpdates);
        table.updateItem(updateItemSpec);

        Speech updatedSpeech = getSpeechById(id);

        return updatedSpeech;
    }

    private void validateResponses(Speech speech) {
        if (speech.getResponses() == null || speech.getResponses().size() == 0) {
            throw new ValidationException("Cannot save a known speech to have no responses");
        }
    }

    private void validateSpeechExists(String id) {
        if (getSpeechById(id) == null) {
            throw new SpeechNotFoundException("Cannot update speech with id " + id + " because it cannot be found");
        }
    }

    @Override
    Table getTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);

    }

    @Override
    String getTableName() {
        return TABLE_NAME;
    }
}
