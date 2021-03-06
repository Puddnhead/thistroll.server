package com.thistroll.service.impl;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.thistroll.data.util.S3ClientProvider;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.service.client.BlogImagesService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class for {@link BlogImagesService}
 *
 * Created by MVW on 7/29/2017.
 */
public class BlogImagesServiceImpl implements BlogImagesService {

    private S3ClientProvider s3ClientProvider;

    private String bucketName;

    private String imageBucketUrl;

    @ThrowsError
    @Override
    public List<String> getImages(String blogId) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName).withPrefix(blogId);
        ObjectListing objects = s3ClientProvider.getS3Client().listObjects(listObjectsRequest);
        return objects.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.charAt(key.length() -1) != '/') // remove directory listing
                .map(filename -> imageBucketUrl + filename)
                .collect(Collectors.toList());
    }

    @Required
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Required
    public void setImageBucketUrl(String imageBucketUrl) {
        this.imageBucketUrl = imageBucketUrl;
    }

    @Required
    public void setS3ClientProvider(S3ClientProvider s3ClientProvider) {
        this.s3ClientProvider = s3ClientProvider;
    }
}
