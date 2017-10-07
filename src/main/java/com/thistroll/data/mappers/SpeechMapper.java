package com.thistroll.data.mappers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.thistroll.domain.Speech;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * Mapper class for mapping items from dynamodb to Speech objects
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechMapper {

    public static Speech mapItemToSpeech(Item item) {
        if (item == null) {
            return null;
        }

        Speech.Builder builder = new Speech.Builder()
                .id(item.getString(Speech.ID_PROPERTY))
                .text(item.getString(Speech.TEXT_PROPERTY));
        if (item.isPresent(Speech.RESPONSES_PROPERTY) && !item.isNull(Speech.RESPONSES_PROPERTY)) {
            builder = builder.responses(item.getList(Speech.RESPONSES_PROPERTY));
        }
        if (item.isPresent(Speech.CREATED_ON_PROPERTY) && !item.isNull(Speech.CREATED_ON_PROPERTY)) {
            builder = builder.createdOn(new DateTime(item.getLong(Speech.CREATED_ON_PROPERTY)));
        }
        if (item.isPresent(Speech.LAST_UPDATED_ON_PROPERTY) && !item.isNull(Speech.LAST_UPDATED_ON_PROPERTY)) {
            builder = builder.lastUpdatedOn(new DateTime(item.getLong(Speech.LAST_UPDATED_ON_PROPERTY)));
        }
        return builder.build();
    }

    public static Speech mapAttributeMapToSpeech(Map<String, AttributeValue> attributeMap) {
        Speech.Builder builder = new Speech.Builder();

        for (String key: attributeMap.keySet()) {
            switch (key) {
                case Speech.ID_PROPERTY:
                    builder = builder.id(attributeMap.get(key).getS());
                    break;
                case Speech.TEXT_PROPERTY:
                    builder = builder.text(attributeMap.get(key).getS());
                    break;
                case Speech.RESPONSES_PROPERTY:
                    builder = builder.responses(attributeMap.get(key).getSS());
                case Speech.CREATED_ON_PROPERTY:
                    AttributeValue createdOnValue = attributeMap.get(key);
                    if (createdOnValue.getN() != null) {
                        builder.createdOn(new DateTime(Long.parseLong(createdOnValue.getN())));
                    }
                    break;
                case Speech.LAST_UPDATED_ON_PROPERTY:
                    AttributeValue lastUpdatedOnValue = attributeMap.get(key);
                    if (lastUpdatedOnValue.getN() != null) {
                        builder.lastUpdatedOn(new DateTime(Long.parseLong(lastUpdatedOnValue.getN())));
                    }
                    break;
            }
        }

        return builder.build();
    }
}
