package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.thistroll.domain.Blog;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Static utility class for mapping DynamoDB responses to blogs
 */
public class BlogMapper {

    /**
     * Maps a query result to a sorted list of blogs, handling cases for null dates
     *
     * @param queryResult result of the query
     * @return a list of blogs
     */
    static List<Blog> mapQueryResultToBlogs(QueryResult queryResult) {
        List<Blog> blogs = new ArrayList<>();

        for (Map<String, AttributeValue> item: queryResult.getItems()) {
            Blog.Builder builder = new Blog.Builder();
            for (String key: item.keySet()) {
                switch (key) {
                    case Blog.ID_PROPERTY:
                        builder.id(item.get(key).getS());
                        break;
                    case Blog.TITLE_PROPERTY:
                        builder.title(item.get(key).getS());
                        break;
                    case Blog.LOCATION_PROPERTY:
                        builder.location(item.get(key).getS());
                        break;
                    case Blog.TEXT_PROPERTY:
                        builder.text(item.get(key).getS());
                        break;
                    case Blog.CREATED_ON_PROPERTY:
                        AttributeValue createdOnValue = item.get(key);
                        if (createdOnValue.getN() != null) {
                            builder.createdOn(new DateTime(Long.parseLong(createdOnValue.getN())));
                        }
                        break;
                    case Blog.LAST_UPDATED_ON_PROPERTY:
                        AttributeValue lastUpdatedOnValue = item.get(key);
                        if (lastUpdatedOnValue.getN() != null) {
                            builder.lastUpdatedOn(new DateTime(Long.parseLong(lastUpdatedOnValue.getN())));
                        }
                        break;
                }
            }
            blogs.add(builder.build());
        }

        return blogs;
    }

    /**
     * Map a scan result to an ordered list of blogs
     *
     * @param scanResult the scan result
     * @return a list of blogs
     */
    static List<Blog> mapScanResultToBlogs(ScanResult scanResult) {
        List<Blog> blogs = new ArrayList<>();

        for (Map<String, AttributeValue> item: scanResult.getItems()) {
            Blog.Builder builder = new Blog.Builder()
                    .id(item.get(Blog.ID_PROPERTY).getS())
                    .title(item.get(Blog.TITLE_PROPERTY).getS());
            if (item.get(Blog.CREATED_ON_PROPERTY) != null) {
                builder.createdOn(new DateTime(Long.parseLong(item.get(Blog.CREATED_ON_PROPERTY).getN())));
            }
            if (item.get(Blog.LOCATION_PROPERTY) != null) {
                builder.location(item.get(Blog.LOCATION_PROPERTY).getS());
            }

            blogs.add(0, builder.build());
        }

        return blogs;
    }

    /**
     * Maps an Item to Blog, handling cases for null dates
     *
     * @return the mapped blog or null if Item is null
     */
    static Blog mapItemToBlog(Item item) {
        if (item == null) {
            return null;
        }

        Blog.Builder builder = new Blog.Builder()
                .id(item.getString(Blog.ID_PROPERTY))
                .title(item.getString(Blog.TITLE_PROPERTY))
                .location(item.getString(Blog.LOCATION_PROPERTY))
                .text(item.getString(Blog.TEXT_PROPERTY));
        if (item.isPresent(Blog.CREATED_ON_PROPERTY) && !item.isNull(Blog.CREATED_ON_PROPERTY)) {
            builder = builder.createdOn(new DateTime(item.getLong(Blog.CREATED_ON_PROPERTY)));
        }
        if (item.isPresent(Blog.LAST_UPDATED_ON_PROPERTY) && !item.isNull(Blog.LAST_UPDATED_ON_PROPERTY)) {
            builder = builder.lastUpdatedOn(new DateTime(item.getLong(Blog.LAST_UPDATED_ON_PROPERTY)));
        }
        return builder.build();
    }
}
