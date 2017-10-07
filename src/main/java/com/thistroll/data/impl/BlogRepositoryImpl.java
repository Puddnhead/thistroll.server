package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.thistroll.data.api.BlogRepository;
import com.thistroll.data.mappers.BlogMapper;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.exceptions.ValidationException;
import com.thistroll.domain.Blog;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.request.UpdateBlogRequest;
import com.thistroll.service.client.dto.response.GetBlogsResponse;
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

    /**
     * Caches responses to the pageable get blogs requests - does not contain blog text
     */
    private List<Blog> listCache = new ArrayList<>();

    /**
     * Caches responses to the get blog requests - contains text
     */
    private Map<String, Blog> itemCache = new HashMap<>();

    /**
     * Cached reference to the current blog
     */
    private Blog currentBlog = null;

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

        // clear blog list cache and add new blog to item cache
        clearListCache();
        itemCache.put(createdBlog.getId(), createdBlog);
        currentBlog = createdBlog;
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

        // clear list cache and update cache if the blog is already there
        clearListCache();
        if (itemCache.containsKey(updatedBlog.getId())) {
            itemCache.put(updatedBlog.getId(), updatedBlog);
        }
        if (currentBlog != null && currentBlog.getId().equals(updatedBlog.getId())) {
            currentBlog = updatedBlog;
        }

        return updatedBlog;
    }

    @Override
    public Blog findById(String id) {
        Blog blog;

        // if already cached, no need to make a DB call
        if (itemCache.containsKey(id)) {
            blog = itemCache.get(id);
        } else {
            // else fetch it from DB
            blog = fetchFromDBById(id);
            if (blog != null) {
                itemCache.put(id, blog);
            }
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
        // first check if we cached it already
        if (currentBlog != null) {
            return currentBlog;
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
        itemCache.put(blog.getId(), blog);

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

        // delete from item cache if applicable
        if (itemCache.containsKey(id)) {
            itemCache.remove(id);
        }

        // delete from list cache if applicable
        int foundIndex = -1;
        for (int i = 0; i < listCache.size(); i++) {
            if (listCache.get(i).getId().equals(id)) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            listCache.remove(foundIndex);
        }

        // unset the cached current blog if applicable
        if (currentBlog != null && currentBlog.getId().equals(id)) {
            currentBlog = null;
        }

        return Outcome.SUCCESS;
    }

    @Override
    public GetBlogsResponse getPageableBlogList(int pageNumber, int pageSize) {
        int start = pageNumber * pageSize,
                end = start + pageSize;

        while (!allBlogsFetched && listCache.size() < end) {
            fetchNextBlogPage();
        }

        if (listCache.size() <= start) {
            return new GetBlogsResponse(Collections.emptyList(), true);
        }

        end = listCache.size() >= end ? end : listCache.size();

        List<Blog> blogs = listCache.subList(start, end);
        boolean isLastPage = allBlogsFetched && end == listCache.size();
        return new GetBlogsResponse(blogs, isLastPage);
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
                    .withProjectionExpression(StringUtils.join(Arrays.asList(
                            Blog.ID_PROPERTY, Blog.TITLE_PROPERTY, Blog.LOCATION_PROPERTY, Blog.CREATED_ON_PROPERTY), ','))
                    .withExclusiveStartKey(lastEvaluatedKey)
                    .withScanIndexForward(false)
                    .withKeyConditionExpression("#key = :val")
                    .withExpressionAttributeNames(Collections.singletonMap("#key", Blog.PARTITION_KEY_NAME))
                    .withExpressionAttributeValues(Collections.singletonMap(
                            ":val", new AttributeValue().withS(Blog.PARTITION_KEY_VALUE)));
            QueryResult queryResult = amazonDynamoDB.query(queryRequest);
            lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null || lastEvaluatedKey.size() == 0) {
                allBlogsFetched = true;
            }

            List<Blog> blogs = BlogMapper.mapQueryResultToBlogs(queryResult);
            listCache.addAll(blogs);
        }
    }

    /**
     * Checks that the blog has required fields id, title, and text
     *
     * @param blog the blog to validate
     * @throws ValidationException if the blog is missing a required field
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
     * Utility function to clear the list cache and reset the all blogs fetched flag
     */
    private void clearListCache() {
        listCache.clear();
        allBlogsFetched = false;
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
