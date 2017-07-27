package com.thistroll.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.thistroll.domain.Blog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BlogRepositoryImpl}. Not stictly speaking a unit test, as this tests the functionality of
 * {@link BlogMapper} as well
 *
 * Created by MVW on 7/13/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogRepositoryImplTest {

    @InjectMocks
    private BlogRepositoryImpl repository;

    @Mock
    private DynamoDBConnectionProvider connectionProvider;

    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @Mock
    private DynamoDB dynamoDB;

    @Mock
    private Table blogTable;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String LOCATION = "location";
    public static final String TEXT = "text";
    public static final DateTime CREATED_ON = new DateTime();
    public static final DateTime LAST_UPDATED_ON = new DateTime(CREATED_ON.getMillis());

    @Before
    public void setup() throws Exception {
        repository.setConnectionProvider(connectionProvider);
        when(connectionProvider.getDynamoDB()).thenReturn(dynamoDB);
        when(connectionProvider.getAmazonDynamoDB()).thenReturn(amazonDynamoDB);
        when(dynamoDB.getTable(anyString())).thenReturn(blogTable);
    }

    @Test
    public void testCreateReturnsNewInstance() throws Exception {
        Blog original = new Blog.Builder()
                .title(TITLE)
                .text(TEXT)
                .location(LOCATION)
                .build();

        when(blogTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        repository.create(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(Blog.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(Blog.TITLE_PROPERTY), is(TITLE));
        assertThat(item.getString(Blog.LOCATION_PROPERTY), is(LOCATION));
        assertThat(item.getString(Blog.TEXT_PROPERTY), is(TEXT));
        assertThat(item.getLong(Blog.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(Blog.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test
    public void testFindByIdWithDates() throws Exception {
        Item item = new Item().withPrimaryKey(Blog.ID_PROPERTY, ID)
                .withString(Blog.TITLE_PROPERTY, TITLE)
                .withString(Blog.LOCATION_PROPERTY, LOCATION)
                .withString(Blog.TEXT_PROPERTY, TEXT)
                .withLong(Blog.CREATED_ON_PROPERTY, CREATED_ON.getMillis())
                .withLong(Blog.LAST_UPDATED_ON_PROPERTY, LAST_UPDATED_ON.getMillis());

        when(blogTable.getItem(any(GetItemSpec.class))).thenReturn(item);

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

        when(blogTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Blog result = repository.findById(ID);
        assertCommonFields(result);
        assertThat(result.getCreatedOn(), is(nullValue()));
        assertThat(result.getLastUpdatedOn(), is(nullValue()));
    }

    @Test
    public void testGetAllBlogs() throws Exception {
        final long OLD_TIME = new DateTime().getMillis() - 1000;
        final long RECENT_TIME = new DateTime().getMillis();

        Map<String, AttributeValue> recentBlog = new HashMap<>();
        recentBlog.put(Blog.ID_PROPERTY, new AttributeValue().withS("1"));
        recentBlog.put(Blog.TITLE_PROPERTY, new AttributeValue().withS("BLOG1"));
        recentBlog.put(Blog.CREATED_ON_PROPERTY, new AttributeValue().withN(RECENT_TIME + ""));

        Map<String, AttributeValue> oldBlog = new HashMap<>();
        oldBlog.put(Blog.ID_PROPERTY, new AttributeValue().withS("2"));
        oldBlog.put(Blog.TITLE_PROPERTY, new AttributeValue().withS("BLOG2"));
        oldBlog.put(Blog.CREATED_ON_PROPERTY, new AttributeValue().withN(OLD_TIME + ""));

        ScanResult scanResult = mock(ScanResult.class);
        when(scanResult.getItems()).thenReturn(Arrays.asList(oldBlog, recentBlog));
        when(amazonDynamoDB.scan(any(ScanRequest.class))).thenReturn(scanResult);

        List<Blog> result = repository.getAllBlogs();
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is("1"));
        assertThat(result.get(1).getId(), is("2"));
        assertThat(result.get(0).getTitle(), is("BLOG1"));
        assertThat(result.get(1).getTitle(), is("BLOG2"));
        assertThat(result.get(0).getCreatedOn(), is(new DateTime(RECENT_TIME)));
        assertThat(result.get(1).getCreatedOn(), is(new DateTime(OLD_TIME)));
    }

    private void assertCommonFields(Blog blog) {
        assertThat(blog.getId(), is(ID));
        assertThat(blog.getTitle(), is(TITLE));
        assertThat(blog.getLocation(), is(LOCATION));
        assertThat(blog.getText(), is(TEXT));
    }
}