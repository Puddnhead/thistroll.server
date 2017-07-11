package com.thistroll.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.thistroll.service.client.BlogService;

/**
 * Created by micha on 7/11/2017.
 * Implementation for {@link com.thistroll.service.client.BlogService}
 */
public class BlogServiceImpl implements BlogService {
    @Override
    public String getMostRecentBlog() {
        try {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                    .build();
            DynamoDB dynamoDB = new DynamoDB(client);
            Table table = dynamoDB.getTable("TestTable");
            int id = 1;

            GetItemSpec spec = new GetItemSpec().withPrimaryKey("Id", id);
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            return "" + outcome.get("text");
        }
        catch (Exception e) {
            System.err.println("Unable to read item");
            System.err.println(e.getMessage());
        }
        return null;
    }
}
