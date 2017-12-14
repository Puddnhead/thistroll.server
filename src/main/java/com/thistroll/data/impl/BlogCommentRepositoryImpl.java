package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.thistroll.data.api.BlogCommentRepository;
import com.thistroll.data.mappers.BlogCommentMapper;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.BlogComment;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Implementation for {@link BlogCommentRepository}
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentRepositoryImpl implements BlogCommentRepository {

    public static final String TABLE_NAME = "blog_comment";

    private static final int MAX_COMMENT_LENGTH = 512;

    private DynamoDBConnectionProvider connectionProvider;

    @Override
    public BlogComment addCommentToBlog(BlogComment blogComment) {
        validateBlogComment(blogComment);
        BlogComment createdBlogComment = createBlogCommentWithIdAndGeneratedDates(blogComment);
        Table table = getBlogCommentTable();

        // Required Fields
        Item item = new Item().withPrimaryKey(BlogComment.BLOG_ID_PROPERTY, createdBlogComment.getBlogId(),
                BlogComment.ID_PROPERTY, createdBlogComment.getId())
                .withString(BlogComment.COMMENT_PROPERTY, createdBlogComment.getComment())
                .withString(BlogComment.USERNAME_PROPERTY, createdBlogComment.getUsername())
                .withLong(BlogComment.CREATED_ON_PROPERTY, createdBlogComment.getCreatedOn().getMillis())
                .withLong(BlogComment.LAST_UPDATED_ON_PROPERTY, createdBlogComment.getLastUpdatedOn().getMillis());

        // persist item
        table.putItem(item);

        return createdBlogComment;
    }

    @Override
    public List<BlogComment> getCommentsForBlog(String blogId) {
        List<BlogComment> blogComments = new ArrayList<>();
        AmazonDynamoDB amazonDynamoDB = connectionProvider.getAmazonDynamoDB();
        Map<String, AttributeValue> lastEvaluatedKey;

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TABLE_NAME)
                .withIndexName(BlogComment.CREATED_ON_INDEX)
                .withScanIndexForward(true)
                .withKeyConditionExpression("#key = :val")
                .withExpressionAttributeNames(Collections.singletonMap("#key", BlogComment.BLOG_ID_PROPERTY))
                .withExpressionAttributeValues(Collections.singletonMap(
                        ":val", new AttributeValue().withS(blogId)));

        do {
            QueryResult queryResult = amazonDynamoDB.query(queryRequest);
            lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            queryRequest = queryRequest.withExclusiveStartKey(lastEvaluatedKey);
            blogComments.addAll(BlogCommentMapper.mapQueryResultToBlogComments(queryResult));
        } while (lastEvaluatedKey != null);

        return blogComments;
    }

    @Override
    public Outcome deleteBlogComment(BlogComment blogComment) {

        Table table = getBlogCommentTable();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(BlogComment.BLOG_ID_PROPERTY, blogComment.getBlogId(), BlogComment.ID_PROPERTY, blogComment.getId()))
                .withReturnValues(ReturnValue.ALL_OLD);

        DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
        if (outcome.getDeleteItemResult().getAttributes() == null) {
            return Outcome.FAILURE;
        }

        return Outcome.SUCCESS;
    }

    private Table getBlogCommentTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);
    }

    private void validateBlogComment(BlogComment blogComment) {
        if (StringUtils.isEmpty(blogComment.getBlogId())) {
            throw new ValidationException("Must specify the blog to add the comment to");
        }

        if (StringUtils.isEmpty(blogComment.getComment())) {
            throw new ValidationException("Cannot add an empty comment");
        }

        if (blogComment.getComment().length() > MAX_COMMENT_LENGTH) {
            throw new ValidationException("Comment exceeds maximum length of " + MAX_COMMENT_LENGTH + " characters");
        }

        if (StringUtils.isEmpty(blogComment.getUsername())) {
            throw new ValidationException("Cannot add a comment without a username");
        }
    }

    private BlogComment createBlogCommentWithIdAndGeneratedDates(BlogComment blogComment) {
        DateTime now = new DateTime();
        return new BlogComment.Builder()
                .id(UUID.randomUUID().toString())
                .createdOn(now)
                .lastUpdatedOn(now)
                .comment(blogComment.getComment())
                .username(blogComment.getUsername())
                .blogId(blogComment.getBlogId())
                .build();
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
