package com.thistroll.data;

import com.thistroll.domain.Blog;
import jdk.nashorn.internal.ir.annotations.Ignore;
import jdk.nashorn.internal.runtime.ECMAException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Integration test for BlogRepository
 *
 * Created by MVW on 7/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test-context.xml")
//@Ignore
public class BlogRepositoryImplIntegrationTest {

    @Autowired
    BlogRepository blogRepository;

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
    public void testGetMostRecentBlog() throws Exception {
//        Blog blog1 = createBlogWithTitleAndText("Blog1", "Blog 1 Text");
//        blog1 = blogRepository.create(blog1);
//        Thread.sleep(2);
//
//        Blog blog2 = createBlogWithTitleAndText("Blog2", "Blog 2 Text");
//        blog2 = blogRepository.create(blog2);

        Blog blog1 = blogRepository.findById("afd898a4-ea63-42fb-b50d-46591293aecc");
        Blog blog2 = blogRepository.findById("d1ab46c9-17a4-4169-8c06-1cfce1fb04df");

        Blog result = blogRepository.getMostRecentBlog();
        assertThat(result, is(blog2));

        blogRepository.deleteBlog(blog1.getId());
        blogRepository.deleteBlog(blog2.getId());
    }

    private Blog createBlogWithTitleAndText(String title, String text) {
        return new Blog.Builder()
                .title(title)
                .text(text)
                .build();
    }
}