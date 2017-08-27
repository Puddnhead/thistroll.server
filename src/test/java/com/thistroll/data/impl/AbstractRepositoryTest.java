package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by MVW on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractRepositoryTest {

    @Mock
    protected DynamoDBConnectionProvider connectionProvider;

    @Mock
    protected AmazonDynamoDB amazonDynamoDB;

    @Mock
    protected DynamoDB dynamoDB;

    @Mock
    protected Table mockTable;

    @Captor
    protected ArgumentCaptor<Item> itemCaptor;

    @Before
    public void baseSetup() throws Exception {
        setConnectionProvider(connectionProvider);
        when(connectionProvider.getDynamoDB()).thenReturn(dynamoDB);
        when(connectionProvider.getAmazonDynamoDB()).thenReturn(amazonDynamoDB);
        when(dynamoDB.getTable(anyString())).thenReturn(mockTable);
    }

    abstract void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider);

}
