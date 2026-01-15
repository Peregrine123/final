package com.jiyu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "collection")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name="cid")//实际上是把 category 对象的 id 属性作为 cid 进行了查询。
    private Category category;

    String cover;
    String title;
    String cast;
    String date;
    String press;
    String summary;
}
