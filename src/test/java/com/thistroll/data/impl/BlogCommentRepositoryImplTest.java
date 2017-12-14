package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.BlogComment;
import com.thistroll.exceptions.ValidationException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Repository test for {@link BlogCommentRepositoryImpl}
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentRepositoryImplTest extends AbstractRepositoryTest {

    @InjectMocks
    private BlogCommentRepositoryImpl blogCommentRepository;

    private static final String COMMENT = "Some comment";
    private static final String BLOG_ID = "abc";
    private static final String ID = "def";
    private static final String USERNAME = "mr_jones";

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        blogCommentRepository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Test
    public void testAddCommentCreatesNewInstance() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .comment(COMMENT)
                .blogId(BLOG_ID)
                .username(USERNAME)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        blogCommentRepository.addCommentToBlog(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(BlogComment.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(BlogComment.BLOG_ID_PROPERTY), is(BLOG_ID));
        assertThat(item.getString(BlogComment.USERNAME_PROPERTY), is(USERNAME));
        assertThat(item.getString(BlogComment.COMMENT_PROPERTY), is(COMMENT));
        assertThat(item.getLong(BlogComment.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(BlogComment.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test(expected = ValidationException.class)
    public void testAddCommentFailsWithoutComment() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .username(USERNAME)
                .blogId(BLOG_ID)
                .build();
        blogCommentRepository.addCommentToBlog(original);
    }

    @Test(expected = ValidationException.class)
    public void testAddCommentFailsWithoutUsername() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .comment(COMMENT)
                .blogId(BLOG_ID)
                .build();
        blogCommentRepository.addCommentToBlog(original);
    }

    @Test(expected = ValidationException.class)
    public void testAddCommentFailsWithoutBlogId() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .username(USERNAME)
                .comment(COMMENT)
                .build();
        blogCommentRepository.addCommentToBlog(original);
    }

    @Test
    public void testGetCommentsForBlog() throws Exception {
        List<Map<String, AttributeValue>> attributeValueMaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            attributeValueMaps.add(createAttributeValueMap());
        }
        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getItems()).thenReturn(attributeValueMaps);
        doAnswer(invocationOnMock -> {
            QueryRequest queryRequest = (QueryRequest)invocationOnMock.getArguments()[0];
            if (queryRequest.getExclusiveStartKey() == null) {
                Map<String, AttributeValue> lastEvaluatedKey = new HashMap<>();
                lastEvaluatedKey.put(BlogComment.BLOG_ID_PROPERTY, new AttributeValue().withS(BLOG_ID));
                lastEvaluatedKey.put(BlogComment.CREATED_ON_PROPERTY, new AttributeValue().withN(new DateTime().getMillis() + ""));
                when(queryResult.getLastEvaluatedKey()).thenReturn(lastEvaluatedKey);
            } else {
                when(queryResult.getLastEvaluatedKey()).thenReturn(null);
            }
            return queryResult;
        }).when(amazonDynamoDB).query(any(QueryRequest.class));

        List<BlogComment> blogComments = blogCommentRepository.getCommentsForBlog(BLOG_ID);
        assertThat(blogComments.size(), is(10));

    }

    private Map<String, AttributeValue> createAttributeValueMap() {
        Map<String, AttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(BlogComment.ID_PROPERTY, new AttributeValue().withS(ID));
        attributeValueMap.put(BlogComment.BLOG_ID_PROPERTY, new AttributeValue().withS(BLOG_ID));
        attributeValueMap.put(BlogComment.COMMENT_PROPERTY, new AttributeValue().withS(COMMENT));
        attributeValueMap.put(BlogComment.USERNAME_PROPERTY, new AttributeValue().withS(USERNAME));
        return attributeValueMap;
    }
}