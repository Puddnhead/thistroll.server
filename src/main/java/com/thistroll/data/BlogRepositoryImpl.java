package com.thistroll.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.thistroll.domain.Blog;
import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

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
        throw new NotImplementedException("No updating yet");
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
    public void deleteBlog(String id) {
        Table table = getBlogTable();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, id));

        table.deleteItem(deleteItemSpec);
    }

    @Override
    public List<Blog> getAllBlogs() {
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME)
                .withIndexName(Blog.CREATED_ON_INDEX)
                .withAttributesToGet(Blog.ID_PROPERTY, Blog.TITLE_PROPERTY, Blog.CREATED_ON_PROPERTY);

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
