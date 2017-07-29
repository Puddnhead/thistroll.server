package com.thistroll.data;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Created by MVW on 7/29/2017.
 */
public class S3ClientProvider {

    private final AmazonS3 s3Client =
            AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).build();

    public AmazonS3 getS3Client() {
        return s3Client;
    }
}
