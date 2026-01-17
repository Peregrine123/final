package com.jiyu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPage {
    private List<BlogArticle> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;

    public static BlogPage from(Page<BlogArticle> pageData) {
        if (pageData == null) {
            return new BlogPage();
        }
        return new BlogPage(
                pageData.getContent(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }
}
