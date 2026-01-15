package com.jiyu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "blog_article")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class BlogArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String articleTitle;

    private String articleContentHtml;

    private String articleContentMd;

    private String articleAbstract;

    private String articleCover;


    private Date articleDate;

}


