package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

        table.putItem(new Item().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, createdBlog.getId())
                        .withString(Blog.TITLE_PROPERTY, createdBlog.getTitle())
                        .withString(Blog.LOCATION_PROPERTY, createdBlog.getLocation())
                        .withString(Blog.TEXT_PROPERTY, createdBlog.getText())
                        .withLong(Blog.CREATED_ON_PROPERTY, createdBlog.getCreatedOn().getMillis())
                        .withLong(Blog.LAST_UPDATED_ON_PROPERTY, createdBlog.getLastUpdatedOn().getMillis()));

        return createdBlog;
    }

    @Override
    public Blog update(UpdateBlogRequest request) {
        Table table = getBlogTable();

        List<AttributeUpdate> attributeUpdates = new ArrayList<>();
        attributeUpdates.add(new AttributeUpdate(Blog.LAST_UPDATED_ON_PROPERTY).put(new DateTime().getMillis()));
        attributeUpdates.add(new AttributeUpdate(Blog.LOCATION_PROPERTY).put(request.getLocation()));
        if (StringUtils.isNotEmpty(request.getTitle())) {
            attributeUpdates.add(new AttributeUpdate(Blog.TITLE_PROPERTY).put(request.getTitle()));
        }
        if (StringUtils.isNotEmpty(request.getText())) {
            attributeUpdates.add(new AttributeUpdate(Blog.TEXT_PROPERTY).put(request.getText()));
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, request.getBlogId())
                .withAttributeUpdate(attributeUpdates);
        table.updateItem(updateItemSpec);

        return findById(request.getBlogId());
    }

    @Override
    public Blog findById(String id) {
        Table table = getBlogTable();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE,
                Blog.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);

        return BlogMapper.mapItemToBlog(outcome);
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

        List<Blog> blogs = BlogMapper.mapQueryResultToBlogs(queryResult);
        return blogs.get(0);
    }

    @Override
    public Outcome deleteBlog(String id) {
        Table table = getBlogTable();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, id))
                .withReturnValues(ReturnValue.ALL_OLD);

        DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
        if (outcome.getDeleteItemResult().getAttributes() == null) {
            return Outcome.FAILURE;
        }
        return Outcome.SUCCESS;
    }

    @Override
    public List<Blog> getAllBlogs() {
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME)
                .withIndexName(Blog.CREATED_ON_INDEX)
                .withAttributesToGet(Blog.ID_PROPERTY, Blog.TITLE_PROPERTY, Blog.LOCATION_PROPERTY, Blog.CREATED_ON_PROPERTY);

        ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
        return BlogMapper.mapScanResultToBlogs(scanResult);
    }

    @Override
    public List<Blog> getPageableBlogList(int pageNumber, int pageSize) {
        throw new NotImplementedException("Not pageable yet");
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
                .location(blog.getLocation())
                .text(blog.getText())
                .createdOn(now)
                .lastUpdatedOn(now)
                .build();
    }

    private Table getBlogTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
