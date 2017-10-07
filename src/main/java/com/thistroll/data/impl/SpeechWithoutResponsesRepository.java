package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.Speech;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Required;

/**
 * Repository for holding speech that has no responses. This is a separate repository so it can be easily scanned when
 * supplying answers.
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechWithoutResponsesRepository extends SpeechRepositoryBase {

    public static final String TABLE_NAME = "speech_without_responses";

    @Override
    public Speech saveSpeech(Speech speech) {
        // clear responses and then call super class implementation
        Speech copy = new Speech.Builder()
                .text(speech.getText())
                .build();

        return super.saveSpeech(copy);
    }

    @Override
    public Speech updateSpeech(Speech speech) {
        throw new NotImplementedException("If you have responses to update you're using the wrong repository");
    }

    Table getTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);
    }

    String getTableName() {
        return TABLE_NAME;
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
