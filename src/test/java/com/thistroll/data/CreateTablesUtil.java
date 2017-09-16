package com.thistroll.data;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.thistroll.domain.Blog;
import com.thistroll.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to generate a local DynamoDB schema
 *
 * Created by MVW on 7/13/2017.
 */
public class CreateTablesUtil {

    public static void main(String[] args) {
        createUserTable();
    }

    private static void createBlogTable() {
        DynamoDB dynamoDB = createConnection();
        final String TABLE_NAME = "blog";

        try {
            System.out.println("Attempting to create table; please wait...");

            // Attribute definitions
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(
                    new AttributeDefinition()
                        .withAttributeName(Blog.PARTITION_KEY_NAME)
                        .withAttributeType("S"),
                    new AttributeDefinition()
                        .withAttributeName(Blog.ID_PROPERTY)
                        .withAttributeType("S"),
                    new AttributeDefinition()
                        .withAttributeName(Blog.CREATED_ON_PROPERTY)
                        .withAttributeType("N")
            );

            // Table key schema
            List<KeySchemaElement> tableKeySchema = Arrays.asList(
                new KeySchemaElement()
                    .withAttributeName(Blog.PARTITION_KEY_NAME)
                    .withKeyType(KeyType.HASH),
                new KeySchemaElement()
                    .withAttributeName(Blog.ID_PROPERTY)
                    .withKeyType(KeyType.RANGE));  //Partition key

            // CreatedOn Index
            GlobalSecondaryIndex createdOnIndex = new GlobalSecondaryIndex()
                    .withIndexName(Blog.CREATED_ON_INDEX)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 10)
                            .withWriteCapacityUnits((long) 1))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

            List<KeySchemaElement> indexKeySchema = new ArrayList<>();

            indexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(Blog.PARTITION_KEY_NAME)
                    .withKeyType(KeyType.HASH));  //Partition key
            indexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(Blog.CREATED_ON_PROPERTY)
                    .withKeyType(KeyType.RANGE));  //Sort key

            createdOnIndex.setKeySchema(indexKeySchema);


            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 5)
                            .withWriteCapacityUnits((long) 1))
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(tableKeySchema)
                    .withGlobalSecondaryIndexes(createdOnIndex);

            Table table = dynamoDB.createTable(createTableRequest);
            table.waitForActive();
            System.out.println(table.getDescription());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    private static void createUserTable() {
        DynamoDB dynamoDB = createConnection();
        final String TABLE_NAME = "thistroll_user";

        try {
            System.out.println("Attempting to create table; please wait...");

            // Attribute definitions
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(
                    new AttributeDefinition()
                            .withAttributeName(User.PARTITION_KEY_NAME)
                            .withAttributeType("S"),
                    new AttributeDefinition()
                            .withAttributeName(User.ID_PROPERTY)
                            .withAttributeType("S"),
                    new AttributeDefinition()
                            .withAttributeName(User.USERNAME_PROPERTY)
                            .withAttributeType("S"),
                    new AttributeDefinition()
                            .withAttributeName(User.EMAIL_PROPERTY)
                            .withAttributeType("S")
            );

            // Table key schema
            List<KeySchemaElement> tableKeySchema = Arrays.asList(
                    new KeySchemaElement()
                            .withAttributeName(User.PARTITION_KEY_NAME)
                            .withKeyType(KeyType.HASH),
                    new KeySchemaElement()
                            .withAttributeName(User.ID_PROPERTY)
                            .withKeyType(KeyType.RANGE));  //Partition key

            // Username Index
            GlobalSecondaryIndex usernameIndex = new GlobalSecondaryIndex()
                    .withIndexName(User.USERNAME_INDEX)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(10L)
                            .withWriteCapacityUnits(1L))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

            List<KeySchemaElement> usernameIndexKeySchema = new ArrayList<>();
            usernameIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(User.PARTITION_KEY_NAME)
                    .withKeyType(KeyType.HASH));  //Partition key
            usernameIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(User.USERNAME_PROPERTY)
                    .withKeyType(KeyType.RANGE));  //Sort key
            usernameIndex.setKeySchema(usernameIndexKeySchema);

            // Email index
            GlobalSecondaryIndex emailIndex = new GlobalSecondaryIndex()
                    .withIndexName(User.EMAIL_INDEX)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(10L)
                            .withWriteCapacityUnits(1L))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

            List<KeySchemaElement> emailIndexKeySchema = new ArrayList<>();
            emailIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(User.PARTITION_KEY_NAME)
                    .withKeyType(KeyType.HASH));
            emailIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(User.EMAIL_PROPERTY)
                    .withKeyType(KeyType.RANGE));
            emailIndex.setKeySchema(emailIndexKeySchema);

            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 5)
                            .withWriteCapacityUnits((long) 1))
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(tableKeySchema)
                    .withGlobalSecondaryIndexes(usernameIndex, emailIndex);

            Table table = dynamoDB.createTable(createTableRequest);
            table.waitForActive();
            System.out.println(table.getDescription());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    private static DynamoDB createConnection() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();

        return new DynamoDB(client);
    }
}
