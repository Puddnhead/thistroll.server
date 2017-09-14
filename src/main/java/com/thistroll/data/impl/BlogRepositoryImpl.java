package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.Lists;
import com.thistroll.data.api.BlogRepository;
import com.thistroll.data.exceptions.ValidationException;
import com.thistroll.domain.Blog;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Repository to fetch blogs from dynamoDB. Caches results in memory and updates in memory as well if the items have
 * been fetched to the cache.
 *
 * Created by MVW on 7/13/2017.
 */
public class BlogRepositoryImpl implements BlogRepository {

    private DynamoDBConnectionProvider connectionProvider;

    private static final String TABLE_NAME = "blog";

    private List<Blog> blogListCache = new ArrayList<>();

    private Map<String, Blog> idToBlogMapCache = new HashMap<>();

    private Map<String, AttributeValue> lastEvaluatedKey = null;

    private boolean allBlogsFetched = false;

    @Override
    public Blog create(Blog blog) {
        validateBlog(blog);
        Blog createdBlog = createBlogWithGeneratedIdAndDates(blog);
        Table table = getBlogTable();

        // Required Fields
        Item item = new Item().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE, Blog.ID_PROPERTY, createdBlog.getId())
                        .withString(Blog.TITLE_PROPERTY, createdBlog.getTitle())
                        .withString(Blog.TEXT_PROPERTY, createdBlog.getText())
                        .withLong(Blog.CREATED_ON_PROPERTY, createdBlog.getCreatedOn().getMillis())
                        .withLong(Blog.LAST_UPDATED_ON_PROPERTY, createdBlog.getLastUpdatedOn().getMillis());

        // Optional Fields
        if (StringUtils.isNotEmpty(createdBlog.getLocation())) {
            item = item.withString(Blog.LOCATION_PROPERTY, createdBlog.getLocation());
        }

        // persist item
        table.putItem(item);

        // add the blog to the beginning of the cache
        blogListCache.add(0, createdBlog);
        idToBlogMapCache.put(createdBlog.getId(), createdBlog);
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

        Blog updatedBlog = fetchFromDBById(request.getBlogId());

        // update cache if the blog is already there
        if (idToBlogMapCache.containsKey(updatedBlog.getId())) {
            idToBlogMapCache.put(updatedBlog.getId(), updatedBlog);

            int foundIndex = -1;
            for (int index = 0; index < blogListCache.size(); index++) {
                if (blogListCache.get(index).getId().equals(updatedBlog.getId())) {
                    foundIndex = index;
                    break;
                }
            }
            if (foundIndex != -1) {
                blogListCache.set(foundIndex, updatedBlog);
            }
        }

        return updatedBlog;
    }

    @Override
    public Blog findById(String id) {
        Blog blog = null;

        // if already cached, no need to make a DB call
        if (idToBlogMapCache.containsKey(id)) {
            blog = idToBlogMapCache.get(id);
        } else {
            // else fetch it from DB
            blog = fetchFromDBById(id);
        }

        return blog;
    }

    private Blog fetchFromDBById(String id) {
        Table table = getBlogTable();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey(Blog.PARTITION_KEY_NAME, Blog.PARTITION_KEY_VALUE,
                Blog.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);

        return BlogMapper.mapItemToBlog(outcome);
    }

    @Override
    public Blog getMostRecentBlog() {
        // if in cache, don't make a DB call
        if (blogListCache.size() > 0) {
            return blogListCache.get(0);
        }

        // else fetch from DB
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
        Blog blog = blogs.get(0);

        // add to cache
        blogListCache.add(0, blog);
        idToBlogMapCache.put(blog.getId(), blog);

        return blog;
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

        // delete from cache if applicable
        if (idToBlogMapCache.containsKey(id)) {
            idToBlogMapCache.remove(id);

            int foundIndex = -1;
            for (int i = 0; i < blogListCache.size(); i++) {
                if (blogListCache.get(i).getId().equals(id)) {
                    foundIndex = i;
                    break;
                }
            }
            if (foundIndex != -1) {
                blogListCache.remove(foundIndex);
            }
        }
        return Outcome.SUCCESS;
    }

    @Override
    public List<Blog> getPageableBlogList(int pageNumber, int pageSize) {
        int start = pageNumber * pageSize,
                end = start + pageSize;

        while (!allBlogsFetched && blogListCache.size() < end) {
            fetchNextBlogPage();
        }

        if (blogListCache.size() <= start) {
            return Collections.emptyList();
        }

        end = blogListCache.size() >= end ? end : blogListCache.size();

        return blogListCache.subList(start, end);
    }

    /**
     * Unless the "allBlogsFetched" flag has been set, fetch the next page of blogs and add it to the local cache
     */
    private void fetchNextBlogPage() {
        if (!allBlogsFetched) {
            AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
            QueryRequest queryRequest = new QueryRequest()
                    .withTableName(TABLE_NAME)
                    .withIndexName(Blog.CREATED_ON_INDEX)
                    .withAttributesToGet(Blog.ID_PROPERTY, Blog.TITLE_PROPERTY, Blog.LOCATION_PROPERTY, Blog.CREATED_ON_PROPERTY)
                    .withExclusiveStartKey(lastEvaluatedKey)
                    .withScanIndexForward(false);

            QueryResult queryResult = amazonDynamoDB.query(queryRequest);
            lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null || lastEvaluatedKey.size() == 0) {
                allBlogsFetched = true;
            }

            List<Blog> blogs = BlogMapper.mapQueryResultToBlogs(queryResult);
            blogListCache.addAll(blogs);
            blogs.forEach(blog -> idToBlogMapCache.put(blog.getId(), blog));
        }
    }

    /**
     * Checks that the blog has required fields id, title, and text
     *
     * @param blog the blog to validate
     * @throws com.thistroll.data.exceptions.ValidationException if the blog is missing a required field
     */
    private void validateBlog(Blog blog) {
        if (StringUtils.isEmpty(blog.getTitle())) {
            throw new ValidationException("Cannot create blog without a title");
        }

        if (StringUtils.isEmpty(blog.getText())) {
            throw new ValidationException("Cannot create blog without text");
        }
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
