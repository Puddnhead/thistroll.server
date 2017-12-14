package com.thistroll.data.mappers;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.thistroll.domain.BlogComment;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mapper for blog comments
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentMapper {

    public static List<BlogComment> mapQueryResultToBlogComments(QueryResult queryResult) {
        List<BlogComment> blogComments = new ArrayList<>();

        for (Map<String, AttributeValue> item: queryResult.getItems()) {
            BlogComment.Builder builder = new BlogComment.Builder();
            for (String key: item.keySet()) {
                switch (key) {
                    case BlogComment.ID_PROPERTY:
                        builder.id(item.get(key).getS());
                        break;
                    case BlogComment.COMMENT_PROPERTY:
                        builder.comment(item.get(key).getS());
                        break;
                    case BlogComment.BLOG_ID_PROPERTY:
                        builder.blogId(item.get(key).getS());
                        break;
                    case BlogComment.USERNAME_PROPERTY:
                        builder.username(item.get(key).getS());
                    case BlogComment.CREATED_ON_PROPERTY:
                        AttributeValue createdOnValue = item.get(key);
                        if (createdOnValue.getN() != null) {
                            builder.createdOn(new DateTime(Long.parseLong(createdOnValue.getN())));
                        }
                        break;
                    case BlogComment.LAST_UPDATED_ON_PROPERTY:
                        AttributeValue lastUpdatedOnValue = item.get(key);
                        if (lastUpdatedOnValue.getN() != null) {
                            builder.lastUpdatedOn(new DateTime(Long.parseLong(lastUpdatedOnValue.getN())));
                        }
                        break;
                }
            }
            blogComments.add(builder.build());
        }

        return blogComments;
    }
}
