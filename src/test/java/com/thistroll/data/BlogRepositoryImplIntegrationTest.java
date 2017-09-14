package com.thistroll.data;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Integration test for BlogRepository
 *
 * Created by MVW on 7/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-repository-test-context.xml")
@Ignore
public class BlogRepositoryImplIntegrationTest {

    @Autowired
    BlogRepository blogRepository;

    public static final String LOCATION = "Lost in Latin America";

    @Test
    public void testCreateGetDelete() throws Exception {
        Blog blog = createBlogWithTitleAndText("Some title", "Some text");
        Blog createdBlog = blogRepository.create(blog);
        System.out.println("Created Successfully:\n" + createdBlog.toString());

        Blog fetchedBlog = blogRepository.findById(createdBlog.getId());
        System.out.println("Fetched Successfully:\n" + fetchedBlog.toString());

        blogRepository.deleteBlog(fetchedBlog.getId());
        System.out.println("Deleted Successfully");
    }

    @Test
    public void testGetMostRecentBlogAndGetBlogs() throws Exception {
        Blog blog1 = createBlogWithTitleAndText("Blog1", "Blog 1 Text");
        blog1 = blogRepository.create(blog1);
        Thread.sleep(2);

        Blog blog2 = createBlogWithTitleAndText("Blog2", "Blog 2 Text");
        blog2 = blogRepository.create(blog2);

        // Most Recent should be blog 2
        Blog result = blogRepository.getMostRecentBlog();
        assertThat(result, is(blog2));

        // getBlogs() should return the most recent first
        List<Blog> allBlogs = blogRepository.getPageableBlogList(0, 2).getBlogs();
        assertThat(allBlogs.get(0).getId(), is(blog2.getId()));
        assertThat(allBlogs.get(1).getId(), is(blog1.getId()));
        assertThat(allBlogs.get(0).getTitle(), is(blog2.getTitle()));
        assertThat(allBlogs.get(1).getTitle(), is(blog1.getTitle()));
        assertThat(allBlogs.get(0).getCreatedOn(), is(blog2.getCreatedOn()));
        assertThat(allBlogs.get(1).getCreatedOn(), is(blog1.getCreatedOn()));

        blogRepository.deleteBlog(blog1.getId());
        blogRepository.deleteBlog(blog2.getId());
    }

    private Blog createBlogWithTitleAndText(String title, String text) {
        return new Blog.Builder()
                .title(title)
                .text(text)
                .location(LOCATION)
                .build();
    }
}