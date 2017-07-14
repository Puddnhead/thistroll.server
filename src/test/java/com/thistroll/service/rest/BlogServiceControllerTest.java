package com.thistroll.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thistroll.data.BlogRepository;
import com.thistroll.domain.Blog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Integration Tests for blog service
 *
 * Created by MVW on 7/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:spring-rest-test-context.xml")
public class BlogServiceControllerTest {

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    BlogRepository blogRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public static final String GENERATED_ID = "generatedId";
    public static final DateTime NOW = new DateTime();
    public static final Blog MOCK_BLOG = new Blog.Builder().title("blah").build();

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();

        doAnswer(invocationOnMock -> {
            Blog blog = (Blog) invocationOnMock.getArguments()[0];
            return new Blog.Builder()
                    .id(GENERATED_ID)
                    .title(blog.getTitle())
                    .text(blog.getText())
                    .createdOn(NOW)
                    .lastUpdatedOn(NOW)
                    .build();
        }).when(blogRepository).create(any(Blog.class));

        doAnswer(invocationOnMock -> {
            String id = (String)invocationOnMock.getArguments()[0];
            return new Blog.Builder()
                    .id(id)
                    .title("some title")
                    .build();
        }).when(blogRepository).findById(anyString());

        when(blogRepository.getMostRecentBlog()).thenReturn(MOCK_BLOG);
    }

    @Test
    public void testCreateBlog() throws Exception {
        Blog blog = new Blog.Builder()
                .title("Drunk in Oaxaca")
                .text("Listening to ...And Out Come the Wolves")
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
        MvcResult mvcResult = mockMvc.perform(get("/blog?id=blah"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody, containsString("blah"));
    }

    @Test
    public void testGetCurrentBlog() throws Exception {
        mockMvc.perform(get("/blog/current"))
                .andExpect(status().isOk());
    }
}