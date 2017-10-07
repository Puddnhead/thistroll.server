package com.thistroll.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.thistroll.data.util.S3ClientProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link BlogImagesServiceImpl}
 *
 * Created by MVW on 7/29/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogImagesServiceImplTest {

    @InjectMocks
    private BlogImagesServiceImpl service;

    @Mock
    private S3ClientProvider s3ClientProvider;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private ObjectListing objectListing;

    @Mock
    private S3ObjectSummary folderSummary;

    @Mock
    private S3ObjectSummary fileSummary;

    private static final String BUCKET_URL = "http://bucket/";
    private static final String BUCKET_NAME = "chocolate";
    private static final String FOLDER_KEY = "folder/";
    private static final String FILE_KEY = "folder/file";

    @Before
    public void setup() {
        service.setBucketName(BUCKET_NAME);
        service.setImageBucketUrl(BUCKET_URL);

        when(s3ClientProvider.getS3Client()).thenReturn(amazonS3);
        when(amazonS3.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(folderSummary, fileSummary));
        when(folderSummary.getKey()).thenReturn(FOLDER_KEY);
        when(fileSummary.getKey()).thenReturn(FILE_KEY);
    }

    @Test
    public void testIgnoresDirectories() throws Exception {
        List<String> images = service.getImages("id");
        assertThat(images.size(), is(1));
        assertThat(images.get(0), is(BUCKET_URL + FILE_KEY));
    }

    @Test
    public void testNoDirectory() throws Exception {
        when(objectListing.getObjectSummaries()).thenReturn(emptyList());
        List<String> images = service.getImages("id");
        assertThat(images, is(emptyList()));
    }
}