package com.thistroll.service.rest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.thistroll.data.util.S3ClientProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Rest test for {@link BlogImagesServiceController}
 *
 * Created by MVW on 7/29/2017.
 */
public class BlogImagesServiceControllerTest extends ControllerTestBase {

    private AmazonS3 amazonS3 = mock(AmazonS3.class);
    private ObjectListing objectListing = mock(ObjectListing.class);
    private S3ObjectSummary folderSummary = mock(S3ObjectSummary.class);
    private S3ObjectSummary fileSummary = mock(S3ObjectSummary.class);

    private static final String FOLDER_KEY = "bogus/";
    private static final String FILE_KEY = "folder/file";

    @Autowired
    private S3ClientProvider s3ClientProvider;

    @Before
    public void setup() {
        when(s3ClientProvider.getS3Client()).thenReturn(amazonS3);
        when(amazonS3.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(folderSummary, fileSummary));
        when(folderSummary.getKey()).thenReturn(FOLDER_KEY);
        when(fileSummary.getKey()).thenReturn(FILE_KEY);
    }

    @Test
    public void testGetImages() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/blog/id/images"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody.contains(FILE_KEY), is(true));
        assertThat(responseBody.contains(FOLDER_KEY), is(false));
    }
}
