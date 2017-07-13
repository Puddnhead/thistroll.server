package com.thistroll.data;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Package protected connection provider for the DynamoDB database
 *
 * Created by MVW on 7/13/2017.
 */
class DynamoDBConnectionProvider {

    private AmazonDynamoDB amazonDynamoDB;

    private DynamoDB dynamoDB;

    /**
     * Default region for local configurations
     */
    private String defaultRegion = "us-east-2";

    /**
     * Creates an instance of dynamoDB with or without a URL endpoint
     */
    public DynamoDBConnectionProvider(String endpointUrl) {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();

        if (StringUtils.isNotEmpty(endpointUrl)) {
            builder = builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl,
                    defaultRegion));
        }

        amazonDynamoDB = builder.build();
        dynamoDB = new DynamoDB(amazonDynamoDB);
    }

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }
}
