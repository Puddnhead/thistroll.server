package com.thistroll.service.rest;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.dto.request.UpdateBlogRequest;
import com.thistroll.service.client.dto.response.GetBlogsResponse;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Tests for blog service
 *
 * Created by MVW on 7/13/2017.
 */
public class BlogServiceControllerTest extends ControllerTestBase {

    @Autowired
    private BlogRepository blogRepository;

    private static final String GENERATED_ID = "generatedId";
    private static final DateTime NOW = new DateTime();
    private static final Blog MOCK_BLOG = new Blog.Builder().title("blah").build();

    @Test
    public void testCreateBlog() throws Exception {
        doAnswer(invocationOnMock -> {
            Blog blog = (Blog) invocationOnMock.getArguments()[0];
            return new Blog.Builder()
                    .id(GENERATED_ID)
                    .title(blog.getTitle())
                    .location(blog.getLocation())
                    .text(blog.getText())
                    .createdOn(NOW)
                    .lastUpdatedOn(NOW)
                    .build();
        }).when(blogRepository).create(any(Blog.class));

        Blog blog = new Blog.Builder()
                .title("Drunk in Oaxaca")
                .text("Listening to ...And Out Come the Wolves")
                .location("Oaxaca")
                .build();

        String serializedBlog = objectMapper.writeValueAsString(blog);
        MvcResult mvcResult = mockMvc.perform(post("/blog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedBlog))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody, containsString(GENERATED_ID));
        assertThat(responseBody.contains("null"), is(false));
    }

    @Test
    public void testGetBlog() throws Exception {
        doAnswer(invocationOnMock -> {
            String id = (String)invocationOnMock.getArguments()[0];
            return new Blog.Builder()
                    .id(id)
                    .title("some title")
                    .build();
        }).when(blogRepository).findById(anyString());

        MvcResult mvcResult = mockMvc.perform(get("/blog/blah"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody, containsString("blah"));
    }

    @Test
    public void testGetCurrentBlog() throws Exception {
        when(blogRepository.getMostRecentBlog()).thenReturn(MOCK_BLOG);
        mockMvc.perform(get("/blog/current"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBlogs() throws Exception {
        when(blogRepository.getPageableBlogList(anyInt(), anyInt()))
                .thenReturn(new GetBlogsResponse(Collections.singletonList(MOCK_BLOG), true));
        mockMvc.perform(get("/blog/page?pageNumber=4&pageSize=10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateBlog() throws Exception {
        UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest.Builder()
                .id(GENERATED_ID)
                .build();
        String serializedUpdateRequest = objectMapper.writeValueAsString(updateBlogRequest);
        when(blogRepository.findById(GENERATED_ID)).thenReturn(MOCK_BLOG);
        when(blogRepository.update(any(UpdateBlogRequest.class))).thenReturn(MOCK_BLOG);

        mockMvc.perform(put("/blog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedUpdateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteBlog() throws Exception {
        mockMvc.perform(delete("/blog/bogusid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}