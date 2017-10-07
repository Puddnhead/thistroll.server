package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.thistroll.data.api.SpeechRepository;
import com.thistroll.data.mappers.SpeechMapper;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.ValidationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import java.util.Map;


/**
 * Abstract base class for speech repositories
 *
 * Created by MVW on 10/6/2017.
 */
public abstract class SpeechRepositoryBase implements SpeechRepository {

    DynamoDBConnectionProvider connectionProvider;

    abstract Table getTable();
    abstract String getTableName();

    @Override
    public Speech getSpeechById(String id) {
        Table table = getTable();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey(Speech.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);

        return SpeechMapper.mapItemToSpeech(outcome);
    }

    @Override
    public Speech getSpeechByText(String text) {
        return getSpeechById(hashText(text));
    }

    @Override
    public Speech saveSpeech(Speech speech) {
        validateSpeech(speech);

        Table table = getTable();
        Speech copy = createSpeechWithGeneratedIdAndDates(speech);
        Item item = new Item()
                .withPrimaryKey(Speech.ID_PROPERTY, copy.getId(), Speech.CREATED_ON_PROPERTY, copy.getCreatedOn().getMillis())
                .withString(Speech.TEXT_PROPERTY, copy.getText())
                .withLong(Speech.CREATED_ON_PROPERTY, copy.getCreatedOn().getMillis())
                .withLong(Speech.LAST_UPDATED_ON_PROPERTY, copy.getLastUpdatedOn().getMillis());

        // Optional fields
        if (copy.getResponses().size() > 0) {
            item = item.withList(Speech.RESPONSES_PROPERTY, copy.getResponses());
        }

        table.putItem(item);
        return copy;
    }

    @Override
    public Outcome deleteSpeech(String id) {
        Table table = getTable();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(Speech.ID_PROPERTY, id))
                .withReturnValues(ReturnValue.ALL_OLD);

        DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
        if (outcome.getDeleteItemResult().getAttributes() == null) {
            return Outcome.FAILURE;
        }
        return Outcome.SUCCESS;
    }

    @Override
    public Speech getNextSpeech() {
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(getTableName())
                .withLimit(1);
        ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
        if (scanResult.getCount() != 1) {
            return null;
        }

        Map<String, AttributeValue> attributeMap = scanResult.getItems().get(0);
        return SpeechMapper.mapAttributeMapToSpeech(attributeMap);
    }


    @Override
    public int getTotalNumberOfSpeeches() {
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        DescribeTableResult result = amazonDynamoDB.describeTable(getTableName());
        return result.getTable().getItemCount().intValue();
    }

    private void validateSpeech(Speech speech) {
        if (StringUtils.isEmpty(speech.getText())) {
            throw new ValidationException("Speech must have text");
        }
    }

    private Speech createSpeechWithGeneratedIdAndDates(Speech speech) {
        DateTime now = new DateTime();
        return new Speech.Builder()
                .id(hashText(speech.getText()))
                .responses(speech.getResponses())
                .text(speech.getText())
                .createdOn(now)
                .lastUpdatedOn(now)
                .build();
    }

    String hashText(String text) {
        return DigestUtils.sha1Hex(text);
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
