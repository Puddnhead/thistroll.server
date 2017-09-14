package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.thistroll.data.exceptions.ValidationException;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BlogRepositoryImpl}. Not stictly speaking a unit test, as this tests the functionality of
 * {@link BlogMapper} as well
 *
 * Created by MVW on 7/13/2017.
 */
public class BlogRepositoryImplTest extends AbstractRepositoryTest {

    @InjectMocks
    private BlogRepositoryImpl repository;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String LOCATION = "location";
    private static final String TEXT = "text";
    private static final DateTime CREATED_ON = new DateTime();
    private static final DateTime LAST_UPDATED_ON = new DateTime(CREATED_ON.getMillis());

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        repository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Test
    public void testCreateReturnsNewInstance() throws Exception {
        Blog original = new Blog.Builder()
                .title(TITLE)
                .text(TEXT)
                .location(LOCATION)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        repository.create(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(Blog.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(Blog.TITLE_PROPERTY), is(TITLE));
        assertThat(item.getString(Blog.LOCATION_PROPERTY), is(LOCATION));
        assertThat(item.getString(Blog.TEXT_PROPERTY), is(TEXT));
        assertThat(item.getLong(Blog.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(Blog.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test(expected = ValidationException.class)
    public void testCreateFailsForMissingTitle() throws Exception {
        Blog blog = new Blog.Builder().text(TEXT).build();
        repository.create(blog);
    }

    @Test(expected = ValidationException.class)
    public void testCreateFailsForMissingText() throws Exception {
        Blog blog = new Blog.Builder().title(TITLE).build();
        repository.create(blog);
    }

    @Test
    public void testFindByIdWithDates() throws Exception {
        Item item = new Item().withPrimaryKey(Blog.ID_PROPERTY, ID)
                .withString(Blog.TITLE_PROPERTY, TITLE)
                .withString(Blog.LOCATION_PROPERTY, LOCATION)
                .withString(Blog.TEXT_PROPERTY, TEXT)
                .withLong(Blog.CREATED_ON_PROPERTY, CREATED_ON.getMillis())
                .withLong(Blog.LAST_UPDATED_ON_PROPERTY, LAST_UPDATED_ON.getMillis());

        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Blog result = repository.findById(ID);
        assertCommonFields(result);
        assertThat(result.getCreatedOn(), is(CREATED_ON));
        assertThat(result.getLastUpdatedOn(), is(LAST_UPDATED_ON));
    }

    @Test
    public void testFindByIdWithNullDates() throws Exception {
        Item item = new Item().withPrimaryKey(Blog.ID_PROPERTY, ID)
                .withString(Blog.TITLE_PROPERTY, TITLE)
                .withString(Blog.LOCATION_PROPERTY, LOCATION)
                .withString(Blog.TEXT_PROPERTY, TEXT);

        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Blog result = repository.findById(ID);
        assertCommonFields(result);
        assertThat(result.getCreatedOn(), is(nullValue()));
        assertThat(result.getLastUpdatedOn(), is(nullValue()));
    }

    @Test
    public void testGetPageableBlogListWithPagination() throws Exception {
        QueryResult queryResultPage1 = mock(QueryResult.class);
        QueryResult queryResultPage2 = mock(QueryResult.class);

        List<Map<String, AttributeValue>> dbPage1 = new ArrayList<>();
        List<Map<String, AttributeValue>> dbPage2 = new ArrayList<>();
        // service will return most recent blogs first - 9, 8, 7...
        for (int i = 5; i > 0; i--) {
            dbPage1.add(createQueryItem(i + 5));
            dbPage2.add(createQueryItem(i));
        }
        when(queryResultPage1.getItems()).thenReturn(dbPage1);
        when(queryResultPage1.getLastEvaluatedKey()).thenReturn(createQueryItem(5));
        when(queryResultPage2.getItems()).thenReturn(dbPage2);

        doAnswer(invocationOnMock -> {
            QueryRequest queryRequest = (QueryRequest) invocationOnMock.getArguments()[0];
            if (queryRequest.getExclusiveStartKey() == null) {
                return queryResultPage1;
            }
            return queryResultPage2;
        }).when(amazonDynamoDB).query(any(QueryRequest.class));

        List<Blog> blogs = repository.getPageableBlogList(1, 4);
        assertThat(blogs.size(), is(4));
        assertThat(blogs.get(0).getId(), is("6"));
        assertThat(blogs.get(1).getId(), is("5"));
        assertThat(blogs.get(2).getId(), is("4"));
        assertThat(blogs.get(3).getId(), is("3"));
    }

    @Test
    public void testCreateUpdatesCache() throws Exception {
        fetchFiveBlogsToCache();

        // create should add a 6th item to the front of the cache
        Blog createdBlog = new Blog.Builder().title(TITLE).text(TEXT).build();
        repository.create(createdBlog);

        List<Blog> blogs = repository.getPageableBlogList(0, 10);
        assertThat(blogs.size(), is(6));

        createdBlog = blogs.get(0);
        assertThat(createdBlog.getTitle(), is(TITLE));
        assertThat(repository.findById(createdBlog.getId()), is(createdBlog));
    }

    @Test
    public void testUpdateUpdatesCache() throws Exception {
        fetchFiveBlogsToCache();

        Item item = new Item()
                .withString(Blog.ID_PROPERTY, "3")
                .withString(Blog.TITLE_PROPERTY, "3")
                .withString(Blog.LOCATION_PROPERTY, LOCATION);
        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);
        UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest.Builder().blogId("3").location(LOCATION).build();
        repository.update(updateBlogRequest);

        List<Blog> blogs = repository.getPageableBlogList(0, 5);
        assertThat(blogs.size(), is(5));

        Blog updatedBlog = blogs.get(2);
        assertThat(updatedBlog.getId(), is("3"));
        assertThat(updatedBlog.getLocation(), is(LOCATION));
        assertThat(repository.findById("3"), is(updatedBlog));
    }

    @Test
    public void testDeleteUpdatesCache() throws Exception {
        fetchFiveBlogsToCache();
        DeleteItemOutcome deleteItemOutcome = mock(DeleteItemOutcome.class);
        DeleteItemResult deleteItemResult = mock(DeleteItemResult.class);
        Map<String, AttributeValue> attributes = Collections.singletonMap(Blog.ID_PROPERTY, new AttributeValue("5"));
        when(mockTable.deleteItem(any(DeleteItemSpec.class))).thenReturn(deleteItemOutcome);
        when(deleteItemOutcome.getDeleteItemResult()).thenReturn(deleteItemResult);
        when(deleteItemResult.getAttributes()).thenReturn(attributes);

        repository.deleteBlog("5");
        List<Blog> blogs = repository.getPageableBlogList(0, 10);
        assertThat(blogs.size(), is(4));
        assertThat(blogs.get(0).getId(), is("4"));
        assertThat(repository.findById("5"), is(nullValue()));
    }

    // return a map of string to attribute value with the provided id as id and title
    private Map<String, AttributeValue> createQueryItem(int id) {
        Map<String, AttributeValue> blog = new HashMap<>();
        blog.put(Blog.ID_PROPERTY, new AttributeValue().withS(id + ""));
        blog.put(Blog.TITLE_PROPERTY, new AttributeValue().withS(id + ""));
        return blog;
    }

    /**
     * Adds five blogs to cache with ids 5, 4, 3, 2, 1
     */
    private void fetchFiveBlogsToCache() {
        List<Map<String, AttributeValue>> dbPage = new ArrayList<>();
        for (int i = 5; i > 0; i--) {
            dbPage.add(createQueryItem(i));
        }
        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getItems()).thenReturn(dbPage);
        when(amazonDynamoDB.query(any(QueryRequest.class))).thenReturn(queryResult);

        // fetch 5 items into the cache
        List<Blog> blogs = repository.getPageableBlogList(0, 10);
        assertThat(blogs.size(), is(5));
    }

    private void assertCommonFields(Blog blog) {
        assertThat(blog.getId(), is(ID));
        assertThat(blog.getTitle(), is(TITLE));
        assertThat(blog.getLocation(), is(LOCATION));
        assertThat(blog.getText(), is(TEXT));
    }
}