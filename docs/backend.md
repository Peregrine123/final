# 6.4. 后端实现

## 6.4.1. 项目结构

**图6-3 后端项目结构**

后端模块位于 `blc/` 目录，为 Spring Boot（Java 8 + Maven）项目，采用典型的 Controller / Service / DAO 分层结构，并在原有基础上新增了 `dto/`、`config/`、`interceptor/` 等包用于接口数据封装、缓存与安全配置、登录态拦截等能力。当前后端主要结构如下（示意）：

```text
blc/
├── src/main/java/com/jiyu/
│   ├── BlcApplication.java                 Spring Boot 启动类
│   ├── controller/                         控制层：REST API（/api/**）
│   ├── service/                            服务层：业务逻辑、校验、缓存
│   ├── dao/                                DAO层：Spring Data JPA 数据访问
│   ├── pojo/                               实体类/枚举：JPA Entity + 业务模型
│   ├── dto/                                DTO：接口请求/响应对象（短评/收藏/状态等）
│   ├── config/                             配置：Shiro、CORS/拦截器、Redis缓存、Druid等
│   ├── interceptor/                        拦截器：登录态校验（返回401 JSON）
│   ├── realm/                              Shiro Realm：认证信息获取与密码校验
│   ├── result/                             统一返回结构 Result/ResultFactory（登录注册等接口使用）
│   └── error/                              SPA 404 回退到 index.html（前端 history 路由支持）
└── src/main/resources/
    ├── application.properties              端口/DB/Redis/业务开关等配置
    ├── application.yaml                    Druid 数据源调优配置
    └── static/                             后端可直接托管的前端构建产物（演示/一体化部署）
```

另外测试代码位于 `blc/src/test/**`，使用 `application-test.properties` 以 H2 内存库运行，避免依赖本地 MySQL/Redis。

## 6.4.2. Spring Boot配置

为了确保 Spring Boot 应用程序能够正确连接到 MySQL 数据库、使用 JPA 进行数据库操作，并在当前版本中支持 Redis 缓存、短评频控/敏感词、演示数据初始化与文件上传等能力，项目在 `application.properties` / `application.yaml` 中进行了如下关键配置（节选）：
```properties
1.	server.port=8443
2.	spring.datasource.url=jdbc:mysql://127.0.0.1:3306/blc?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
3.	spring.datasource.username=blc
4.	spring.datasource.password=blc123456
5.	spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
6.	spring.thymeleaf.mode=LEGACYHTML5
7.	spring.jpa.hibernate.ddl-auto=update
8.	spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
9.	spring.jpa.show-sql=false
10.	spring.redis.host=127.0.0.1
11.	spring.redis.port=6379
12.	spring.redis.timeout=2000
13.	blc.cache.enabled=true
14.	blc.cache.ttl.list-seconds=300
15.	blc.cache.ttl.detail-seconds=120
16.	blc.cache.ttl.category-seconds=600
17.	blc.demo.seed-user-actions=true
18.	blc.demo.seed-user-actions.favorites=6
19.	blc.demo.seed-user-actions.wish=4
20.	blc.demo.seed-user-actions.watched=4
21.	blc.comment.max-length=280
22.	blc.comment.rate-limit-seconds=10
23.	# blc.comment.blocked-words=spam,scam,敏感词
24.	# blc.upload.dir=${user.dir}/uploads
```

**Code 6-3 Spring Boot配置信息**

除上述配置外，当前后端还包含以下关键“代码级配置”，共同完成登录态管理、跨域、静态资源与缓存等功能：
1) `ShiroConfiguration`：配置 Shiro 的 `SecurityManager`、`Realm` 与 `HashedCredentialsMatcher(md5, iterations=2)`，并注册 `DelegatingFilterProxy` 以保证每个 HTTP 请求都有可用的 `Subject`/`Session`；支持 RememberMe Cookie。
2) `MyWebConfigurer`：注册 `LoginInterceptor` 对 `/api/**` 进行登录拦截（排除 `/api/login`、`/api/register`、`/api/logout`、上传与静态文件等接口），同时配置 CORS（允许 `http://localhost:8080` / `http://127.0.0.1:8080`），并将上传目录映射为 `/api/file/**` 供前端访问封面/头像等资源。
3) `CacheConfig` + `CacheNames`：基于 Spring Cache + Redis 实现列表/详情等热点数据缓存，并支持通过 `blc.cache.enabled` 一键关闭缓存（测试环境默认关闭）。
4) `DruidConfig`：提供 `/druid/*` 监控页面（默认账号 `admin`/`123456`），并在 `application.yaml` 中配置 Druid 连接池参数。
5) `ErrorConfig`：将 404 路由回退到 `/index.html`，用于支持前端 SPA（history 模式）在后端直连访问时的刷新/直达。

另外，项目在测试环境下启用 `test` profile（`blc/src/test/resources/application-test.properties`），核心差异包括：
1) 使用 H2 内存数据库（`MODE=MySQL`）替代本地 MySQL，并将 `spring.jpa.hibernate.ddl-auto=create-drop`；
2) 通过 `spring.jpa.properties.hibernate.globally_quoted_identifiers=true` 规避 `user` 等表名在不同数据库中的关键字冲突；
3) 关闭 Redis 缓存与演示数据初始化（`blc.cache.enabled=false`、`blc.demo.seed-user-actions=false`），使 `mvn test` 可在无外部依赖下稳定运行。

## 6.4.3. 实体类开发

```java
1.	package com.jiyu.pojo;
2.	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
3.	import lombok.*;
4.	import javax.persistence.*;
5.	import java.sql.Date;
6.	@Data
7.	@NoArgsConstructor
8.	@AllArgsConstructor
9.	@Builder
10.	@ToString
11.	@Entity
12.	@Table(name = "blog_article")
13.	@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
14.	public class BlogArticle {
15.	    @Id
16.	    @GeneratedValue(strategy = GenerationType.IDENTITY)
17.	    @Column(name = "id")
18.	    private int id;
19.	    private String articleTitle;
20.	    private String articleContentHtml;
21.	    private String articleContentMd;
22.	    private String articleAbstract;
23.	    private String articleCover;
24.	    private Date articleDate;
25.	}
```
**Code 6-4 实体类BlogArticle**

实体类BlogArticle对应数据库中的 `blog_article` 表，用于保存影评（博客文章）数据。
```java
1.	package com.jiyu.pojo;
2.	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
3.	import lombok.Data;
4.	import lombok.ToString;
5.	import javax.persistence.*;
6.	@Data
7.	@Entity
8.	@Table(name = "category")
9.	@ToString
10.	@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
11.	public class Category {
12.	    @Id
13.	    @GeneratedValue(strategy = GenerationType.IDENTITY)
14.	    @Column(name = "id")
15.	    int id;
16.	    String name;
17.	}
```
**Code 6-5 实体类Category**

实体类Category对应的是数据库的category表，表示电影的分类，主键是id，电影借助标识分类cid可以得到正确的分类类别。
```java
1.	package com.jiyu.pojo;
2.	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
3.	import lombok.Data;
4.	import lombok.ToString;
5.	import javax.persistence.*;
6.	@Data
7.	@Entity
8.	@Table(name = "collection")
9.	@ToString
10.	@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
11.	public class Collection {
12.	    @Id
13.	    @GeneratedValue(strategy = GenerationType.IDENTITY)
14.	    @Column(name = "id")
15.	    int id;
16.	    @ManyToOne
17.	    @JoinColumn(name="cid")
18.	    private Category category;
19.	    String cover;
20.	    String title;
21.	    String cast;
22.	    String date;
23.	    String press;
24.	    String summary;
25.	}
```
**Code 6-6 实体类Collection**

实体类Collection对应collection表，保存收藏电影，显示电影的主要信息。
```java
1.	package com.jiyu.pojo;
2.
3.	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
4.	import lombok.Data;
5.	import lombok.ToString;
6.
7.	import javax.persistence.*;
8.
9.	@Data
10.	@Entity
11.	@Table(name = "movie")
12.	@ToString
13.	@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
14.	public class Movie {
15.	    @Id
16.	    @GeneratedValue(strategy = GenerationType.IDENTITY)
17.	    @Column(name = "id")
18.	    int id;
19.
20.	    @ManyToOne
21.	    @JoinColumn(name="cid")//实际上是把 category 对象的 id 属性作为 cid 进行了查询。
22.	    private Category category;
23.
24.	    String cover;
25.	    String title;
26.	    String cast;
27.	    String date;
28.	    String press;
29.	    String summary;
30.	}
```

**Code 6-7 实体类Movie**

实体类Movie对应数据表movie，保存了一些电影数据，作为项目的数据源。
```java
1.	package com.jiyu.pojo;
2.
3.	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
4.	import lombok.*;
5.
6.	import javax.persistence.*;
7.
8.	@Data
9.	@NoArgsConstructor
10.	@AllArgsConstructor
11.	@Builder
12.	@Entity
13.	@Table(name = "user")
14.	@ToString
15.	@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
16.	public class User {
17.
18.	    @Id
19.	    @GeneratedValue(strategy = GenerationType.IDENTITY)
20.	    @Column(name = "id")
21.	    long id;
22.
23.	    String username;
24.	    String password;
25.	    String salt;
26.	    String role;
27.
28.	    // 新增的字段
29.	    @Column(name = "first_name")
30.	    String firstName;
31.
32.	    @Column(name = "last_name")
33.	    String lastName;
34.
35.	    @Column(name = "email")
36.	    String email;
37.
38.	    @Column(name = "phone")
39.	    String phone;
40.
41.	    @Column(name = "address")
42.	    String address;
43.
44.	    @Column(name = "city")
45.	    String city;
46.
47.	    @Column(name = "state")
48.	    String state;
49.
50.	    @Column(name = "country")
51.	    String country;
52.
53.	    @Column(name = "bio")
54.	    String bio;
55.
56.	    public String getProfilePicture() {
57.	        return profilePicture;
58.	    }
59.
60.	    public void setProfilePicture(String profilePicture) {
61.	        this.profilePicture = profilePicture;
62.	    }
63.
64.	    public String getBio() {
65.	        return bio;
66.	    }
67.
68.	    public void setBio(String bio) {
69.	        this.bio = bio;
70.	    }
71.
72.	    public String getCountry() {
73.	        return country;
74.	    }
75.
76.	    public void setCountry(String country) {
77.	        this.country = country;
78.	    }
79.
80.	    public String getState() {
81.	        return state;
82.	    }
83.
84.	    public void setState(String state) {
85.	        this.state = state;
86.	    }
87.
88.	    public String getCity() {
89.	        return city;
90.	    }
91.
92.	    public void setCity(String city) {
93.	        this.city = city;
94.	    }
95.
96.	    public String getAddress() {
97.	        return address;
98.	    }
99.
100.	    public void setAddress(String address) {
101.	        this.address = address;
102.	    }
103.
104.	    public String getPhone() {
105.	        return phone;
106.	    }
107.
108.	    public void setPhone(String phone) {
109.	        this.phone = phone;
110.	    }
111.
112.	    public String getEmail() {
113.	        return email;
114.	    }
115.
116.	    public void setEmail(String email) {
117.	        this.email = email;
118.	    }
119.
120.	    public String getLastName() {
121.	        return lastName;
122.	    }
123.
124.	    public void setLastName(String lastName) {
125.	        this.lastName = lastName;
126.	    }
127.
128.	    public String getFirstName() {
129.	        return firstName;
130.	    }
131.
132.	    public void setFirstName(String firstName) {
133.	        this.firstName = firstName;
134.	    }
135.
136.	    public String getRole() {
137.	        return role;
138.	    }
139.
140.	    public void setRole(String role) {
141.	        this.role = role;
142.	    }
143.
144.	    public String getSalt() {
145.	        return salt;
146.	    }
147.
148.	    public void setSalt(String salt) {
149.	        this.salt = salt;
150.	    }
151.
152.	    public String getPassword() {
153.	        return password;
154.	    }
155.
156.	    public void setPassword(String password) {
157.	        this.password = password;
158.	    }
159.
160.	    public String getUsername() {
161.	        return username;
162.	    }
163.
164.	    public void setUsername(String username) {
165.	        this.username = username;
166.	    }
167.
168.	    public long getId() {
169.	        return id;
170.	    }
171.
172.	    public void setId(long id) {
173.	        this.id = id;
174.	    }
175.
176.	    @Column(name = "profile_picture")
177.	    String profilePicture;
178.	}
```
**Code 6-8 实体类User**

实体类User表示数据库中的用户实体，利用了 Lombok 简化代码，实现了各项属性的 getter 和 setter 等方法，并通过 JPA 注解定义了与数据库表的映射。这样可以让框架方便地将 Java 对象与数据库中的记录进行映射和操作。

在当前版本中，除上述基础实体外，为支持“收藏/观影状态/短评与点赞”等用户行为模块，还新增了多张关系表对应的实体（均位于 `com.jiyu.pojo` 包中）：
1) `UserFavorite`：用户收藏关系（`user_id` + `movie_id` 唯一），记录收藏时间 `createdAt`；
2) `UserMovieStatus`：用户观影状态（`user_id` + `movie_id` 唯一），用枚举 `MovieWatchStatus(WISH/WATCHED)` 表示“想看/看过”，并记录 `markedAt/createdAt/updatedAt`；
3) `MovieComment`：用户短评/评分（`user_id` + `movie_id` 唯一），支持只评分或只短评；包含 `rating(1-5)`、`content`、`status(PENDING/APPROVED/REJECTED)` 以及创建/更新时间；
4) `MovieCommentLike`：短评点赞关系（`user_id` + `comment_id` 唯一），用于实现点赞/取消点赞的幂等交互；
5) 枚举类：`MovieCommentStatus`、`MovieWatchStatus` 等，用于在代码层面约束字段取值范围、提升可读性与可维护性。

此外，为避免直接暴露 JPA 实体（尤其是懒加载关联字段）带来的序列化问题，并让接口更贴合前端页面使用习惯，项目新增了 `com.jiyu.dto` 包用于封装接口请求/响应：如 `MovieCommentDto`、`MovieCommentPageDto`、`RatingSummaryDto`、`FavoriteStateDto`、`MovieStatusDto` 等。

## 6.4.4. DAO层开发

**图6-4 DAO层代码**

接口用于定义与数据库交互的方法，使用 Spring Data JPA 编写数据访问接口，简化开发过程。当前版本 DAO 层的特点如下：
1) **继承 JpaRepository**：如 `MovieDAO extends JpaRepository<Movie, Integer>`，自动获得常用 CRUD、分页与排序能力；
2) **方法命名派生查询**：通过 `findByUser_IdAndMovie_Id`、`findAllByTitleLikeOrCastLike` 等方法名即可生成 SQL；
3) **自定义 JPQL/聚合查询**：对评分均值/数量、点赞数量统计等场景，通过 `@Query` 编写 JPQL 并返回聚合结果；
4) **幂等接口配合唯一约束**：收藏、点赞、短评等关系表均设置了联合唯一约束（如 `user_id + movie_id`），DAO 层配合 `existsBy.../deleteBy...` 等方法，使“重复收藏/重复点赞/重复取消”都能稳定工作；
5) **新增 DAO 接口**：在原有 `BlogArticleDAO/CategoryDAO/MovieDAO/CollectionDAO/UserDAO` 基础上，新增 `UserFavoriteDAO/UserMovieStatusDAO/MovieCommentDAO/MovieCommentLikeDAO`，支撑“收藏/想看看过/短评点赞/评分统计”等功能。

例如，`MovieCommentDAO` 提供按电影分页查询短评、按用户与电影定位唯一短评、以及评分统计等方法；`MovieCommentLikeDAO` 提供点赞数统计与“我是否点赞过”的查询；`UserMovieStatusDAO` 支持按状态拉取用户的电影列表等。

## 6.4.5. 服务层开发

**图6-5 Service层代码**

Service层用于将业务逻辑与数据访问逻辑分离。它在应用程序中起到了桥梁作用，连接了控制层（Controller层）和数据访问层（DAO层）。
```java
1.	package com.jiyu.service;
2.
3.	import com.jiyu.config.CacheNames;
4.	import com.jiyu.dao.BlogArticleDAO;
5.	import com.jiyu.pojo.BlogArticle;
6.	import com.jiyu.pojo.BlogPage;
7.	import org.springframework.beans.factory.annotation.Autowired;
8.	import org.springframework.cache.annotation.CacheEvict;
9.	import org.springframework.cache.annotation.Cacheable;
10.	import org.springframework.cache.annotation.Caching;
11.	import org.springframework.data.domain.PageRequest;
12.	import org.springframework.data.domain.Sort;
13.	import org.springframework.stereotype.Service;
14.
15.	@Service
16.	public class BlogArticleService {
17.	    @Autowired
18.	    BlogArticleDAO blogArticleDAO;
19.
20.	    @Cacheable(cacheNames = CacheNames.BLOG_LIST, key = "'p:' + #page + ':s:' + #size")
21.	    public BlogPage list(int page, int size) {
22.	        Sort sort = Sort.by(Sort.Direction.DESC, "id");//降序，最新的在前面
23.	        return BlogPage.from(blogArticleDAO.findAll(PageRequest.of(page, size, sort)));
24.	    }
25.
26.	    @Cacheable(cacheNames = CacheNames.BLOG_DETAIL, key = "#id", unless = "#result == null")
27.	    public BlogArticle findById(int id) {
28.	        return blogArticleDAO.findById(id);
29.	    }
30.
31.	    @Caching(evict = {
32.	            @CacheEvict(cacheNames = CacheNames.BLOG_LIST, allEntries = true),
33.	            @CacheEvict(cacheNames = CacheNames.BLOG_DETAIL, key = "#article.id", condition = "#article != null && #article.id > 0")
34.	    })
35.	    public void addOrUpdate(BlogArticle article) {
36.	        blogArticleDAO.save(article);
37.	    }
38.
39.	    @Caching(evict = {
40.	            @CacheEvict(cacheNames = CacheNames.BLOG_LIST, allEntries = true),
41.	            @CacheEvict(cacheNames = CacheNames.BLOG_DETAIL, key = "#id")
42.	    })
43.	    public void delete(int id) {
44.	        blogArticleDAO.deleteById(id);
45.	    }
46.
47.	}
```
**Code 6-9 BlogArticleService**

BlogArticleService实现了对博客影评的核心业务逻辑管理。在当前版本中，该服务在原有 CRUD 基础上引入了 Spring Cache + Redis 缓存：
1. list方法按id降序分页查询所有博客影评，并将分页结果包装为 `BlogPage` 返回；同时使用 `@Cacheable` 按 `page/size` 缓存列表结果以加速访问；
2. findById方法根据影评id查询详情，并缓存到 `blog:detail`；
3. addOrUpdate与delete方法在写操作发生时通过 `@CacheEvict/@Caching` 清理列表与详情缓存，保证缓存与数据库的一致性。
```java
1.	package com.jiyu.service;
2.
3.	import com.jiyu.config.CacheNames;
4.	import com.jiyu.dao.CategoryDAO;
5.	import com.jiyu.pojo.Category;
6.	import org.springframework.beans.factory.annotation.Autowired;
7.	import org.springframework.cache.annotation.Cacheable;
8.	import org.springframework.data.domain.Sort;
9.	import org.springframework.stereotype.Service;
10.
11.	import java.util.List;
12.
13.	@Service
14.	public class CategoryService {
15.	    @Autowired
16.	    CategoryDAO categoryDAO;
17.
18.	    @Cacheable(cacheNames = CacheNames.CATEGORIES_LIST)
19.	    public List<Category> list() {
20.	        Sort sort = Sort.by(Sort.Direction.DESC, "id");
21.	        return categoryDAO.findAll(sort);
22.	    }
23.
24.	    @Cacheable(cacheNames = CacheNames.CATEGORIES_BY_ID, key = "#id", unless = "#result == null")
25.	    public Category get(int id) {
26.	        Category c= categoryDAO.findById(id).orElse(null);
27.	        return c;
28.	    }
29.	}
```
**Code 6-10 CategoryService**

CategoryService类实现了对电影分类（Category）的业务逻辑管理。在当前版本中，该服务引入了缓存以提升分类列表与分类详情的响应速度：
1. list方法按id降序返回分类列表，并通过 `@Cacheable(categories:list)` 缓存结果；
2. get方法根据分类id查询分类对象，并通过 `@Cacheable(categories:byId)` 缓存单条结果（未命中返回null的情况不缓存）。
```java
1.	package com.jiyu.service;
2.
3.	import com.jiyu.config.CacheNames;
4.	import com.jiyu.dao.CollectionDAO;
5.	import com.jiyu.pojo.Category;
6.	import com.jiyu.pojo.Collection;
7.	import org.springframework.beans.factory.annotation.Autowired;
8.	import org.springframework.cache.annotation.CacheEvict;
9.	import org.springframework.cache.annotation.Cacheable;
10.	import org.springframework.cache.annotation.Caching;
11.	import org.springframework.data.domain.Sort;
12.	import org.springframework.stereotype.Service;
13.
14.	import java.util.List;
15.
16.	@Service
17.	public class CollectionService {
18.	    @Autowired
19.	    CollectionDAO collectionDAO;
20.	    @Autowired
21.	    CategoryService categoryService;
22.
23.	    @Cacheable(cacheNames = CacheNames.COLLECTIONS_LIST)
24.	    public List<Collection> list() {
25.	        Sort sort = Sort.by(Sort.Direction.DESC, "id");
26.	        return collectionDAO.findAll(sort);
27.	    }
28.
29.	    //当数据库与id对应的主键存在时更新数据，当主键不存在时插入数据
30.	    @Caching(evict = {
31.	            @CacheEvict(cacheNames = CacheNames.COLLECTIONS_LIST, allEntries = true),
32.	            @CacheEvict(cacheNames = CacheNames.COLLECTIONS_BY_CATEGORY, allEntries = true)
33.	    })
34.	    public void addOrUpdate(Collection collection) {
35.	        collectionDAO.save(collection);
36.	    }
37.
38.	    @Caching(evict = {
39.	            @CacheEvict(cacheNames = CacheNames.COLLECTIONS_LIST, allEntries = true),
40.	            @CacheEvict(cacheNames = CacheNames.COLLECTIONS_BY_CATEGORY, allEntries = true)
41.	    })
42.	    public void deleteById(int id) {
43.	        collectionDAO.deleteById(id);
44.	    }
45.
46.	    @Cacheable(cacheNames = CacheNames.COLLECTIONS_BY_CATEGORY, key = "#cid")
47.	    public List<Collection> listByCategory(int cid) {
48.	        Category category = categoryService.get(cid);
49.	        return collectionDAO.findAllByCategory(category);
50.	    }
51.
52.	    public List<Collection> Search(String keywords) {
53.	        return collectionDAO.findAllByTitleLikeOrCastLike('%' + keywords + '%', '%' + keywords + '%');
54.	    }
55.
56.	}
```
**Code 6-11 CollectionService**

CollectionService类实现了对影单（collection）数据的业务逻辑管理。当前版本在原有增删改查基础上加入缓存策略：
1. list方法缓存全部影单列表（`collections:list`）；
2. listByCategory方法按分类缓存影单列表（`collections:byCategory`）；
3. addOrUpdate与deleteById在写入/删除时清理相关缓存，保证列表缓存不会返回旧数据；
4. Search方法用于关键词检索（标题/演员），该查询通常变化频繁，当前实现未做缓存以避免缓存碎片化。
```java
1.	package com.jiyu.service;
2.
3.	import com.jiyu.config.CacheNames;
4.	import com.jiyu.dao.MovieDAO;
5.	import com.jiyu.pojo.Category;
6.	import com.jiyu.pojo.Movie;
7.	import org.springframework.beans.factory.annotation.Autowired;
8.	import org.springframework.cache.annotation.CacheEvict;
9.	import org.springframework.cache.annotation.Cacheable;
10.	import org.springframework.cache.annotation.Caching;
11.	import org.springframework.data.domain.Sort;
12.	import org.springframework.stereotype.Service;
13.
14.	import java.util.List;
15.
16.	@Service
17.	public class MovieService {
18.	    @Autowired
19.	    MovieDAO movieDAO;
20.	    @Autowired
21.	    CategoryService categoryService;
22.
23.	    @Cacheable(cacheNames = CacheNames.MOVIES_LIST)
24.	    public List<Movie> list() {
25.	        Sort sort = Sort.by(Sort.Direction.DESC, "id");
26.	        return movieDAO.findAll(sort);
27.	    }
28.
29.	    //当数据库与id对应的主键存在时更新数据，当主键不存在时插入数据
30.	    @Caching(evict = {
31.	            @CacheEvict(cacheNames = CacheNames.MOVIES_LIST, allEntries = true),
32.	            @CacheEvict(cacheNames = CacheNames.MOVIES_BY_CATEGORY, allEntries = true)
33.	    })
34.	    public void addOrUpdate(Movie movie) {
35.	        movieDAO.save(movie);
36.	    }
37.
38.	    @Caching(evict = {
39.	            @CacheEvict(cacheNames = CacheNames.MOVIES_LIST, allEntries = true),
40.	            @CacheEvict(cacheNames = CacheNames.MOVIES_BY_CATEGORY, allEntries = true)
41.	    })
42.	    public void deleteById(int id) {
43.	        movieDAO.deleteById(id);
44.	    }
45.
46.	    @Cacheable(cacheNames = CacheNames.MOVIES_BY_CATEGORY, key = "#cid")
47.	    public List<Movie> listByCategory(int cid) {
48.	        Category category = categoryService.get(cid);
49.	        return movieDAO.findAllByCategory(category);
50.	    }
51.
52.	    public List<Movie> Search(String keywords) {
53.	        return movieDAO.findAllByTitleLikeOrCastLike('%' + keywords + '%', '%' + keywords + '%');
54.	    }
55.
56.	}
```
**Code 6-12 MovieService**

MovieService类实现了对电影数据的业务逻辑管理，并结合 Redis 缓存优化了高频访问接口：
1. list方法缓存电影列表（`movies:list`），减少首页/影库页面重复请求带来的数据库压力；
2. listByCategory方法缓存分类下电影列表（`movies:byCategory`）；
3. addOrUpdate与deleteById在写操作后清理列表缓存，避免前端看到旧数据；
4. Search方法用于关键词检索（标题/演员），同样不做缓存以降低缓存碎片化。
```java
1.	package com.jiyu.service;
2.
3.	import com.jiyu.dao.UserDAO;
4.	import com.jiyu.pojo.User;
5.	import org.springframework.beans.factory.annotation.Autowired;
6.	import org.springframework.stereotype.Service;
7.	import org.apache.shiro.crypto.SecureRandomNumberGenerator;
8.	import org.apache.shiro.crypto.hash.SimpleHash;
9.
10.
11.
12.	@Service
13.	public class UserService {
14.
15.	    @Autowired
16.	    UserDAO userDAO;
17.
18.	    public boolean isExist(String username) {
19.	        User user = getByUserName(username);
20.	        return user != null;
21.	    }
22.
23.	    public User getByUserName(String username) {
24.	        return userDAO.findByUsername(username);
25.	    }
26.
27.	    public User get(String username, String password){
28.	        return userDAO.getByUsernameAndPassword(username, password);
29.	    }
30.
31.	    public void add(User user) {
32.	        userDAO.save(user);
33.	    }
34.
35.
36.	    public void update(User user) {
37.	        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
38.	            // 加密新密码
39.	            String password = user.getPassword();
40.	            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
41.	            int times = 2;
42.	            String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
43.	            user.setSalt(salt);
44.	            user.setPassword(encodedPassword);
45.	        } else {
46.	            User existingUser = userDAO.findById(user.getId()).orElse(null);
47.	            if (existingUser != null) {
48.	                user.setPassword(existingUser.getPassword());
49.	                user.setSalt(existingUser.getSalt());
50.	            } else {
51.	                throw new IllegalArgumentException("用户不存在");
52.	            }
53.	        }
54.	        userDAO.save(user);
55.	    }
56.
57.	    public void delete(Long id) {
58.	        userDAO.deleteById(id);
59.	    }
60.
61.	    public User getCurrentUser(String username) {
62.	        return userDAO.findByUsername(username);
63.	    }
64.
65.	    // 新增的根据ID获取用户信息的方法
66.	    public User getUserById(Long id) {
67.	        return userDAO.findById(id).orElse(null);
68.	    }
69.	}
```
**Code 6-15 UserService**

UserService类实现了对用户的数据管理与业务逻辑操作。使用@Service注解标识为服务层组件，并利用@Autowired自动装配UserDAO。该服务提供了检查用户名是否存在、根据用户名和密码获取用户信息、添加和删除用户、更新用户信息等功能。具体来说，isExist方法检查用户名是否已存在；getByUserName方法根据用户名获取用户信息；get方法根据用户名和密码获取用户；add方法添加新用户；delete方法删除指定ID的用户；getCurrentUser方法获取当前用户信息；getUserById方法根据ID获取用户；update方法更新用户信息，且在有新密码时进行加密处理。通过这些功能，UserService提供了一套完整的用户数据管理机制，实现与数据库的交互，并确保数据的一致性和安全性。

除上述基础服务外，当前版本还新增了一组面向“用户行为 + 短评互动”的服务类（位于 `com.jiyu.service`），用于支撑电影详情页的完整交互闭环：
1) `CurrentUserService`：从 Shiro `Subject` 中解析当前登录用户（principal=用户名），并在未登录/会话失效时抛出 401；供各类“/api/me/**”接口复用；
2) `UserFavoriteService`：收藏列表查询、收藏/取消收藏（幂等处理，重复收藏不报错，重复取消不报错）；
3) `UserMovieStatusService`：观影状态（想看/看过）设置与查询、按状态拉取列表，并提供 `markWatched` 供短评提交后自动标记“看过”；
4) `MovieCommentService`：短评/评分的新增与修改（一个用户对同一电影最多一条）、分页列表、评分汇总（均值/人数）、管理员审核状态设置；并包含内容安全与体验相关逻辑（HTML 标签清理、长度限制、敏感词、频率限制等）；
5) `MovieCommentLikeService`：短评点赞/取消点赞与点赞状态查询（同样做幂等处理，并统计点赞数量）；
6) `DemoUserActionsSeeder`：用于演示的可选能力（由配置开关控制），在新用户登录/注册后自动补充收藏/想看/看过数据，避免界面空数据影响展示。

上述服务在错误处理上大量使用 `ResponseStatusException` 返回对应 HTTP 状态码（如 401/403/404/429 等），使前端可以根据状态码统一提示与跳转。

## 6.4.6. 控制器开发

**图6-6 Controller层代码**

Controller层接收并处理用户请求，调用Service层执行具体业务逻辑，并将结果返回给客户端。它负责定义路由、处理异常和返回响应，确保应用的请求和响应流程顺畅。通过控制层，应用实现了用户界面与业务逻辑的有效连接。
```java
1.	package com.jiyu.controller;
2.
3.	import com.jiyu.pojo.BlogArticle;
4.	import com.jiyu.pojo.BlogPage;
5.	import com.jiyu.result.Result;
6.	import com.jiyu.result.ResultFactory;
7.	import com.jiyu.service.BlogArticleService;
8.	import org.springframework.beans.factory.annotation.Autowired;
9.	import org.springframework.web.bind.annotation.*;
10.
11.	import java.text.SimpleDateFormat;
12.	import java.util.Date;
13.
14.	//带admin表示需要权限
15.	@RestController
16.	public class BlogController {
17.	    @Autowired
18.	    BlogArticleService blogArticleService;
19.
20.	    @PostMapping("api/admin/article")
21.	    public Result saveArticle(@RequestBody BlogArticle article) {
22.	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
23.	        article.setArticleDate(java.sql.Date.valueOf(df.format(new Date())));//string=>Date   文章发布时间在后端自动加上去（前端也可）
24.	        blogArticleService.addOrUpdate(article);
25.	        return ResultFactory.buildSuccessResult("保存成功");
26.	    }
27.
28.	    @GetMapping("/api/article/{size}/{page}")
29.	    public BlogPage listArticles(@PathVariable("size") int size, @PathVariable("page") int page) {
30.	        return blogArticleService.list(page - 1, size);//page要减一
31.	    }
32.
33.	    @GetMapping("/api/article/{id}")
34.	    public BlogArticle getOneArticle(@PathVariable("id") int id) {
35.	        return blogArticleService.findById(id);
36.	    }
37.
38.	    @DeleteMapping("/api/admin/article/{id}")
39.	    public Result deleteArticle(@PathVariable("id") int id) {
40.	        blogArticleService.delete(id);
41.	        return ResultFactory.buildSuccessResult("删除成功");
42.	    }
43.	}
```
**Code 6-16 BlogController**

BlogController类实现了对博客影评的管理功能，并使用了Spring Boot的@RestController和相关注解来处理HTTP请求。首先，@Autowired注入了BlogArticleService服务类，实现对博客文章的业务逻辑操作。主要功能如下：
1. saveArticle方法使用POST请求保存博客影评，并自动设置影评发布日期；
2. listArticles方法使用 GET 请求获取分页的博客影评列表，并返回 `BlogPage`（对分页信息与列表内容做了统一包装）；
3. getOneArticle方法使用GET请求根据影评ID获取单个博客影评的详细信息；
4. deleteArticle方法使用DELETE请求根据影评ID删除博客影评。
通过这些方法，BlogController实现了博客影评的增删改查功能，并返回封装的结果给客户端，从而提供了一套完整的博客影评管理的后端接口。
```java
1.	package com.jiyu.controller;
2.
3.
4.	import com.jiyu.pojo.Collection;
5.	import com.jiyu.pojo.Movie;
6.	import com.jiyu.service.CollectionService;
7.	import com.jiyu.service.MovieService;
8.	import com.jiyu.utils.StringUtils;
9.	import org.springframework.beans.factory.annotation.Autowired;
10.	import org.springframework.beans.factory.annotation.Value;
11.	import org.springframework.web.bind.annotation.*;
12.	import org.springframework.web.multipart.MultipartFile;
13.
14.	import javax.servlet.http.HttpServletRequest;
15.	import java.io.File;
16.	import java.text.SimpleDateFormat;
17.	import java.util.Date;
18.	import java.util.List;
19.	import java.nio.file.Files;
20.	import java.nio.file.Path;
21.	import java.nio.file.Paths;
22.
23.
24.	@RestController
25.	public class LibraryController {
26.	    @Autowired
27.	    MovieService movieService;
28.	    @Autowired
29.	    CollectionService collectionService;
30.
31.	    @Value("${blc.upload.dir:${user.dir}/uploads}")
32.	    private String uploadDir;
33.
34.	    @GetMapping("/api/movies")
35.	    public List<Movie> list() throws Exception {
36.	        return movieService.list();
37.	    }
38.
39.	    @PostMapping("/api/admin/movies")
40.	    public Movie addOrUpdate(@RequestBody Movie movie) throws Exception {
41.	        movieService.addOrUpdate(movie);
42.	        return movie;
43.	    }
44.	    @GetMapping("/api/collections")
45.	    public List<Collection> listCollection() throws Exception{
46.	        return collectionService.list();
47.	    }
48.	    @PostMapping("/api/user/add")
49.	    public Collection addOrUpdateCollection(@RequestBody Collection collection) throws Exception {
50.	        collectionService.addOrUpdate(collection);
51.	        return collection;
52.	    }
53.	    @PostMapping("/api/user/delete")
54.	    public void delete(@RequestBody Collection collection) throws Exception {
55.	        collectionService.deleteById(collection.getId());
56.	    }
57.
58.	    @PostMapping("/api/admin/delete")
59.	    public void delete(@RequestBody Movie movie) throws Exception {
60.	        movieService.deleteById(movie.getId());
61.	    }
62.
63.
64.	    @GetMapping("/api/categories/{cid}/movies")
65.	    public List<Movie> listByCategory(@PathVariable("cid") int cid) throws Exception {
66.	        if (0 != cid) {
67.	            return movieService.listByCategory(cid);//查询一类
68.	        } else {
69.	            return list();//查询所有
70.	        }
71.	    }
72.	    @GetMapping("/api/categories/{cid}/collections")
73.	    public List<Collection> listByCategoryCollection(@PathVariable("cid") int cid) throws Exception {
74.	        if (0 != cid) {
75.	            return collectionService.listByCategory(cid);//查询一类
76.	        } else {
77.	            return collectionService.list();//查询所有
78.	        }
79.	    }
80.
81.	    @GetMapping("/api/search")
82.	    public List<Movie> searchResult(@RequestParam("keywords") String keywords) {
83.	        if ("".equals(keywords)) {
84.	            return movieService.list();
85.	        } else {
86.	            return movieService.Search(keywords);
87.	        }
88.	    }
89.
90.	    @PostMapping("api/covers")
91.	    public String coversUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
92.	        System.out.println("文件上传中");
93.	        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
94.
95.	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
96.	        // new Date()为获取当前系统时间
97.	        String format = df.format(new Date());
98.
99.	        String original = file.getOriginalFilename();
100.	        String suffix = "";
101.	        if (original != null) {
102.	            int dot = original.lastIndexOf('.');
103.	            if (dot >= 0 && dot < original.length() - 1) {
104.	                suffix = original.substring(dot);
105.	            }
106.	        }
107.
108.	        String filename = StringUtils.getRandomString(6) + "-" + format + suffix;
109.	        try {
110.	            Files.createDirectories(dir);
111.	            File f = dir.resolve(filename).toFile();
112.	            file.transferTo(f);
113.	            // Return a path (not a fixed host/port) so the frontend can work behind proxies / different deployments.
114.	            String ctx = request.getContextPath();
115.	            if (ctx == null) ctx = "";
116.	            return ctx + "/api/file/" + f.getName(); // served by MyWebConfigurer
117.	        } catch (Exception e) {
118.	            e.printStackTrace();
119.	            return "false";
120.	        }
121.	    }
122.
123.	}
```
**Code 6-17 LibraryController**

LibraryController类实现了对电影和收藏管理功能，并使用Spring Boot的@RestController注解处理HTTP请求。首先，通过@Autowired注入MovieService和CollectionService，实现对电影和收藏的业务逻辑操作。核心功能包括：
1. list方法通过GET请求获取所有电影；
2. addOrUpdate方法使用POST请求添加或更新电影信息；
3. listCollection方法通过GET请求获取所有收藏；
4. addOrUpdateCollection方法使用POST请求添加或更新收藏信息；
5. delete方法通过POST请求删除指定电影或收藏；
6. listByCategory和listByCategoryCollection方法根据分类ID查询相应电影和收藏；
7. searchResult方法根据关键字搜索电影；
8. coversUpload方法处理文件上传，并返回上传文件的URL。
这些功能使得应用程序能够实现电影和收藏的全面管理，提供一整套访问接口，并简化前后端的交互。此外，通过文件上传处理，实现对封面图片的管理。
```java
1.	package com.jiyu.controller;
2.
3.	import com.jiyu.pojo.User;
4.	import com.jiyu.result.Result;
5.	import com.jiyu.result.ResultFactory;
6.	import com.jiyu.service.DemoUserActionsSeeder;
7.	import com.jiyu.service.UserService;
8.	import org.apache.shiro.SecurityUtils;
9.	import org.apache.shiro.authc.AuthenticationException;
10.	import org.apache.shiro.authc.UsernamePasswordToken;
11.	import org.apache.shiro.crypto.SecureRandomNumberGenerator;
12.	import org.apache.shiro.crypto.hash.SimpleHash;
13.	import org.apache.shiro.subject.Subject;
14.	import org.springframework.beans.factory.annotation.Autowired;
15.	import org.springframework.stereotype.Controller;
16.	import org.springframework.web.bind.annotation.*;
17.	import org.springframework.web.util.HtmlUtils;
18.
19.	@Controller
20.	public class LoginController {
21.
22.	    @Autowired
23.	    UserService userService;
24.
25.	    @Autowired
26.	    DemoUserActionsSeeder demoUserActionsSeeder;
27.
28.	    private String normalizeUsername(String username) {
29.	        if (username == null) {
30.	            return "";
31.	        }
32.	        return HtmlUtils.htmlEscape(username).trim();
33.	    }
34.
35.	    @GetMapping(value = "/api/login/check")
36.	    @ResponseBody
37.	    public Result loginCheck(@RequestParam String username) {
38.	        //只能检测用户名是否正确，不能检测密码
39.	        username = normalizeUsername(username);
40.	        if (username.isEmpty()) {
41.	            return ResultFactory.buildFailResult("用户名不能为空");
42.	        }
43.
44.	        boolean exist = userService.isExist(username);
45.	        if (!exist) {
46.	            String message = "用户名不存在";
47.	            return ResultFactory.buildFailResult(message);
48.	        }
49.	        return ResultFactory.buildSuccessResult(username);
50.	    }
51.
52.	    //返回用户的角色
53.	    @ResponseBody
54.	    @GetMapping(value = "api/user/role")
55.	    public Result roleCheck(@RequestParam String username){
56.	        username = normalizeUsername(username);
57.	        if (username.isEmpty()) {
58.	            return ResultFactory.buildFailResult("用户名不能为空");
59.	        }
60.	        User user = userService.getByUserName(username);
61.	        if (user == null) {
62.	            return ResultFactory.buildFailResult("用户名不存在");
63.	        }
64.	        String role = user.getRole();
65.
66.	        System.out.println(role);
67.	        return ResultFactory.buildSuccessResult(role);
68.	    }
69.
70.	    @PostMapping(value = "/api/login")
71.	    @ResponseBody
72.	    public Result login(@RequestBody User requestUser) {
73.	        if (requestUser == null) {
74.	            return ResultFactory.buildFailResult("用户名或密码不能为空");
75.	        }
76.	        String username = normalizeUsername(requestUser.getUsername());
77.	        if (username.isEmpty() || requestUser.getPassword() == null || requestUser.getPassword().trim().isEmpty()) {
78.	            return ResultFactory.buildFailResult("用户名或密码不能为空");
79.	        }
80.	        Subject subject = SecurityUtils.getSubject();
81.
82.	        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, requestUser.getPassword());
83.	        usernamePasswordToken.setRememberMe(true);//开启RememberMe
84.	        try {
85.	            //登录验证
86.	            subject.login(usernamePasswordToken);
87.	            try {
88.	                demoUserActionsSeeder.seedIfEmpty(userService.getByUserName(username));
89.	            } catch (Exception ignore) {
90.	                // Demo seeding must never block login.
91.	            }
92.	            return ResultFactory.buildSuccessResult(username);
93.	        } catch (AuthenticationException e) {
94.	            String message = "账号或密码错误";
95.	            return ResultFactory.buildFailResult(message);
96.	        }
97.	    }
98.
99.	    @GetMapping("api/register/check")
100.	    @ResponseBody
101.	    public Result registerCheck(@RequestParam("username") String username) {
102.
103.	        username = normalizeUsername(username);//Html编码转译,防止恶意注册
104.	        if (username.isEmpty()) {
105.	            return ResultFactory.buildFailResult("用户名不能为空");
106.	        }
107.
108.	        boolean exist = userService.isExist(username);
109.	        if (exist) {
110.	            String message = "用户名已被使用";
111.	            return ResultFactory.buildFailResult(message);
112.	        }
113.	        return ResultFactory.buildSuccessResult(username);
114.	    }
115.
116.	    @PostMapping("api/register")
117.	    @ResponseBody
118.	    public Result register(@RequestBody User user) {
119.
120.	        if (user == null) {
121.	            return ResultFactory.buildFailResult("用户名或密码不能为空");
122.	        }
123.	        String username = normalizeUsername(user.getUsername());
124.	        String password = user.getPassword();
125.	        if (username.isEmpty()) {
126.	            return ResultFactory.buildFailResult("用户名不能为空");
127.	        }
128.	        if (password == null || password.trim().isEmpty()) {
129.	            return ResultFactory.buildFailResult("密码不能为空");
130.	        }
131.	        user.setUsername(username);
132.
133.	        boolean exist = userService.isExist(username);
134.	        if (exist) {
135.	            String message = "用户名已被使用";
136.	            return ResultFactory.buildFailResult(message);
137.	        }
138.
139.	        // 随机生成长度24位的盐
140.	        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
141.	        int times = 2;
142.	        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
143.	        // 存储用户信息，包括 salt 与 hash 后的密码
144.	        user.setSalt(salt);
145.	        user.setPassword(encodedPassword);
146.	        user.setRole("normal");
147.	        userService.add(user);
148.	        try {
149.	            demoUserActionsSeeder.seedIfEmpty(user);
150.	        } catch (Exception ignore) {
151.	            // Demo seeding must never block registration.
152.	        }
153.
154.	        return ResultFactory.buildSuccessResult(user);
155.	    }
156.
157.	    @ResponseBody
158.	    @GetMapping("api/logout")
159.	    public Result logout() {
160.	        Subject subject = SecurityUtils.getSubject();
161.	        subject.logout();//会清除 session、principals，并把 authenticated 设置为 false
162.	        String message = "成功登出";
163.	        return ResultFactory.buildSuccessResult(message);
164.	    }
165.
166.	    @ResponseBody
167.	    @GetMapping(value = "api/authentication")
168.	    public String authentication(){
169.	        return "身份认证成功";
170.	    }
171.
172.
173.
174.	}
```
LoginController类实现了用户登录、注册、身份验证和角色检查等功能。使用@Controller注解标识为控制层组件，并利用@Autowired自动装配UserService。核心功能包括：
1. loginCheck方法通过GET请求检查用户名是否存在，并对空用户名做校验；
2. roleCheck方法通过GET请求返回用户角色，并在用户不存在时返回失败信息；
3. login方法通过POST请求验证用户登录信息，使用Shiro进行安全认证；认证成功后（可选）触发 DemoUserActionsSeeder 自动补充演示数据；
4. registerCheck方法通过GET请求检查用户名是否已被使用，并对空用户名做校验；
5. register方法通过POST请求注册新用户：为密码生成随机盐并使用 md5 + 迭代2次进行加密后入库，同时设置默认角色为 normal；注册成功后（可选）触发演示数据初始化；
6. logout方法通过GET请求处理用户登出，清除会话信息；
7. authentication方法通过GET请求验证用户身份（前端路由守卫会用该接口校验会话是否有效）。
这些功能使得应用程序能够实现用户管理、身份验证和角色检查的全面功能，并在输入校验与演示数据体验方面做了增强。
```java
1.	package com.jiyu.controller;
2.
3.	import com.jiyu.pojo.User;
4.	import com.jiyu.service.UserService;
5.	import org.springframework.beans.factory.annotation.Autowired;
6.	import org.springframework.web.bind.annotation.*;
7.
8.	@RestController
9.	@RequestMapping("/api/users")
10.	public class UserController {
11.
12.	    @Autowired
13.	    private UserService userService;
14.
15.	    @GetMapping("/{id}")
16.	    public User getUserById(@PathVariable Long id) {
17.	        return userService.getUserById(id);
18.	    }
19.
20.	    @GetMapping("/current")
21.	    public User getCurrentUser(@RequestParam String username) {
22.	        return userService.getCurrentUser(username);
23.	    }
24.
25.	    @PostMapping
26.	    public User createUser(@RequestBody User user) {
27.	        userService.add(user);
28.	        return user;
29.	    }
30.
31.	    @PutMapping("/{id}")
32.	    public User updateUser(@PathVariable Long id, @RequestBody User user) {
33.	        user.setId(id);
34.	        userService.update(user);
35.	        return user;
36.	    }
37.
38.	    @DeleteMapping("/{id}")
39.	    public void deleteUser(@PathVariable Long id) {
40.	        userService.delete(id);
41.	    }}
```
**Code 6-18 UserController**

UserController类实现了对用户数据的管理功能，提供了获取、创建、更新和删除用户的接口。通过@RestController注解标识为控制层组件，并利用@Autowired自动装配UserService以处理业务逻辑。核心功能包括：
1. getUserById方法通过GET请求根据用户ID获取用户信息；
2. getCurrentUser方法通过GET请求获取当前用户的信息；
3. createUser方法通过POST请求创建新用户；
4. updateUser方法通过PUT请求更新指定ID的用户信息；
5. deleteUser方法通过DELETE请求删除指定ID的用户。
通过这些方法，UserController实现了对用户数据的全面管理，提供了对外的RESTful API接口，简化了客户端与服务端的交互，并确保用户数据的维护和统一。

在当前版本中，除上述基础控制器外，还新增了“收藏/观影状态/短评与点赞”等接口控制器，用于支撑电影详情页的互动能力（均为 `/api/**` 前缀，且默认受 `LoginInterceptor` 登录拦截保护）：
1) 收藏接口 `UserFavoriteController`（`/api/me/favorites`）
   - GET `/api/me/favorites`：获取“我的收藏”电影列表
   - GET `/api/me/favorites/{movieId}`：获取某电影是否已收藏（以及收藏时间）
   - PUT `/api/me/favorites/{movieId}`：收藏电影（幂等）
   - DELETE `/api/me/favorites/{movieId}`：取消收藏（幂等）
2) 观影状态接口 `UserMovieStatusController`（`/api/me/movie-status`）
   - GET `/api/me/movie-status/{movieId}`：获取某电影的观影状态（想看/看过/未设置）
   - PUT `/api/me/movie-status/{movieId}`：设置观影状态（WISH/WATCHED）
   - GET `/api/me/movie-status?status=WISH`：按状态拉取电影列表（如“我的想看/看过”）
3) 短评与评分接口
   - `MyMovieCommentController`（`/api/me/movie-comments`）：GET/PUT 读写“我的短评”，支持只评分或只短评；
   - `MovieCommentPublicController`（`/api/movies/{movieId}`）：GET 获取短评分页列表与评分汇总（均值/人数）；
4) 点赞接口 `UserMovieCommentLikeController`（`/api/me/movie-comment-likes`）
   - GET `/api/me/movie-comment-likes/{commentId}`：获取我是否点赞 + 点赞数
   - PUT `/api/me/movie-comment-likes/{commentId}`：点赞（幂等）
   - DELETE `/api/me/movie-comment-likes/{commentId}`：取消点赞（幂等）
5) 管理员短评审核接口 `AdminMovieCommentController`（`/api/admin/movie-comments`）
   - PUT `/api/admin/movie-comments/{commentId}/status`：设置短评状态（PENDING/APPROVED/REJECTED），并在服务层校验管理员角色（role=admin）。

说明：新增加的控制器接口多数直接返回 DTO（如 `MovieCommentDto`、`FavoriteStateDto`、`MovieStatusDto` 等）并结合 `ResponseStatusException` 使用标准 HTTP 状态码表达错误（401 未登录、403 无权限、404 不存在、429 频率限制等），便于前端统一处理。
