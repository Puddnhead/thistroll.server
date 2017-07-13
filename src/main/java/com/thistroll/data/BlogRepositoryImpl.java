package com.thistroll.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.thistroll.domain.Blog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created by MVW on 7/13/2017.
 */
public class BlogRepositoryImpl implements BlogRepository {

    private DynamoDBConnectionProvider connectionProvider;

    private static final String TABLE_NAME = "blog";

    @Override
    public Blog create(Blog blog) {
        Blog createdBlog = createBlogWithGeneratedIdAndDates(blog);
        Table table = getBlogTable();

        PutItemOutcome outcome = table
                .putItem(new Item().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, createdBlog.getId())
                        .withString(Blog.TITLE_PROPERTY, createdBlog.getTitle())
                        .withString(Blog.TEXT_PROPERTY, createdBlog.getText())
                        .withLong(Blog.CREATED_ON_PROPERTY, createdBlog.getCreatedOn().getMillis())
                        .withLong(Blog.LAST_UPDATED_ON_PROPERTY, createdBlog.getLastUpdatedOn().getMillis()));

        return createdBlog;
    }

    @Override
    public Blog update(Blog blog) {
        return null;
    }

    @Override
    public Blog findById(String id) {
        Table table = getBlogTable();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE,
                Blog.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);

        return mapItemToBlog(outcome);
    }

    @Override
    public Blog getMostRecentBlog() {
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TABLE_NAME)
                .withIndexName(Blog.CREATED_ON_INDEX)
                .withKeyConditionExpression("#key = :val")
                .withExpressionAttributeNames(Collections.singletonMap("#key", Blog.PARTITION_KEY_NAME))
                .withExpressionAttributeValues(Collections.singletonMap(":val", new AttributeValue().withS(Blog.PARTITION_KEY_VALUE)))
                .withScanIndexForward(false)
                .withLimit(1);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        if (queryResult.getCount() != 1) {
            return null;
        }

        List<Blog> blogs = mapQueryResultToBlogs(queryResult);
        return blogs.get(0);
    }

    @Override
    public void deleteBlog(String id) {
        Table table = getBlogTable();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, id));

        table.deleteItem(deleteItemSpec);
    }

    @Override
    public List<Blog> getPageableBlogList(int pageNumber, int pageSize) {
        return null;
    }

    /**
     * Returns a new instance of blog with a generated ID
     *
     * @param blog the original blog
     * @return a new instance of a Blog with a generated ID
     */
    private Blog createBlogWithGeneratedIdAndDates(Blog blog) {
        DateTime now = new DateTime();

        return new Blog.Builder()
                .id(UUID.randomUUID().toString())
                .title(blog.getTitle())
                .text(blog.getText())
                .createdOn(now)
                .lastUpdatedOn(now)
                .build();
    }

    private Table getBlogTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);
    }

    /**
     * Maps an Item to Blog, handling cases for null dates
     *
     * @return the mapped blog or null if Item is null
     */
    private Blog mapItemToBlog(Item item) {
        if (item == null) {
            return null;
        }

        Blog.Builder builder = new Blog.Builder()
                .id(item.getString(Blog.ID_PROPERTY))
                .title(item.getString(Blog.TITLE_PROPERTY))
                .text(item.getString(Blog.TEXT_PROPERTY));
        if (item.isPresent(Blog.CREATED_ON_PROPERTY) && !item.isNull(Blog.CREATED_ON_PROPERTY)) {
            builder = builder.createdOn(new DateTime(item.getLong(Blog.CREATED_ON_PROPERTY)));
        }
        if (item.isPresent(Blog.LAST_UPDATED_ON_PROPERTY) && !item.isNull(Blog.LAST_UPDATED_ON_PROPERTY)) {
            builder = builder.lastUpdatedOn(new DateTime(item.getLong(Blog.LAST_UPDATED_ON_PROPERTY)));
        }
        return builder.build();
    }

    /**
     * Maps a query result to a sorted list of blogs, handling cases for null dates
     *
     * @param queryResult
     * @return
     */
    private List<Blog> mapQueryResultToBlogs(QueryResult queryResult) {
        List<Blog> blogs = new ArrayList<>();

        for (Map<String, AttributeValue> item: queryResult.getItems()) {
            Blog.Builder builder = new Blog.Builder();
            for (String key: item.keySet()) {
                switch (key) {
                    case Blog.ID_PROPERTY:
                        builder = builder.id(item.get(key).getS());
                        break;
                    case Blog.TITLE_PROPERTY:
                        builder = builder.title(item.get(key).getS());
                        break;
                    case Blog.TEXT_PROPERTY:
                        builder = builder.text(item.get(key).getS());
                        break;
                    case Blog.CREATED_ON_PROPERTY:
                        AttributeValue createdOnValue = item.get(key);
                        if (createdOnValue.getN() != null) {
                            builder = builder.createdOn(new DateTime(Long.parseLong(createdOnValue.getN())));
                        }
                        break;
                    case Blog.LAST_UPDATED_ON_PROPERTY:
                        AttributeValue lastUpdatedOnValue = item.get(key);
                        if (lastUpdatedOnValue.getN() != null) {
                            builder = builder.lastUpdatedOn(new DateTime(Long.parseLong(lastUpdatedOnValue.getN())));
                        }
                        break;
                }
            }
            blogs.add(builder.build());
        }

        return blogs;
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
