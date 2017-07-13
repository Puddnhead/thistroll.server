package com.thistroll.data;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BlogRepositoryImpl}
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
    private DynamoDB dynamoDB;

    @Mock
    private Table blogTable;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final DateTime CREATED_ON = new DateTime();
    public static final DateTime LAST_UPDATED_ON = new DateTime(CREATED_ON.getMillis());

    @Before
    public void setup() throws Exception {
        repository.setConnectionProvider(connectionProvider);
        when(connectionProvider.getDynamoDB()).thenReturn(dynamoDB);
        when(dynamoDB.getTable(anyString())).thenReturn(blogTable);
    }

    @Test
    public void testCreateReturnsNewInstance() throws Exception {
        Blog original = new Blog.Builder()
                .title(TITLE)
                .text(TEXT)
                .build();

        when(blogTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));
        repository.create(original);

        Item item = itemCaptor.getValue();
        assertThat(item.getString(Blog.ID_PROPERTY), is(not(nullValue())));
        assertThat(item.getString(Blog.TITLE_PROPERTY), is(TITLE));
        assertThat(item.getString(Blog.TEXT_PROPERTY), is(TEXT));
        assertThat(item.getLong(Blog.CREATED_ON_PROPERTY) > 0L, is(true));
        assertThat(item.getLong(Blog.LAST_UPDATED_ON_PROPERTY) > 0L, is(true));
    }

    @Test
    public void testFindByIdWithDates() throws Exception {
        Item item = new Item().withPrimaryKey(Blog.ID_PROPERTY, ID)
                .withString(Blog.TITLE_PROPERTY, TITLE)
                .withString(Blog.TEXT_PROPERTY, TEXT)
                .withLong(Blog.CREATED_ON_PROPERTY, CREATED_ON.getMillis())
                .withLong(Blog.LAST_UPDATED_ON_PROPERTY, LAST_UPDATED_ON.getMillis());

        when(blogTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Blog result = repository.findById(ID);
        assertThat(result.getId(), is(ID));
        assertThat(result.getTitle(), is(TITLE));
        assertThat(result.getText(), is(TEXT));
        assertThat(result.getCreatedOn(), is(CREATED_ON));
        assertThat(result.getLastUpdatedOn(), is(LAST_UPDATED_ON));
    }

    @Test
    public void testFindByIdWithNullDates() throws Exception {
        Item item = new Item().withPrimaryKey(Blog.ID_PROPERTY, ID)
                .withString(Blog.TITLE_PROPERTY, TITLE)
                .withString(Blog.TEXT_PROPERTY, TEXT);

        when(blogTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        Blog result = repository.findById(ID);
        assertThat(result.getId(), is(ID));
        assertThat(result.getTitle(), is(TITLE));
        assertThat(result.getText(), is(TEXT));
        assertThat(result.getCreatedOn(), is(nullValue()));
        assertThat(result.getLastUpdatedOn(), is(nullValue()));
    }
}