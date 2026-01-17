# 数据库表结构（表格版）

> 本项目后端使用 Spring Data JPA + Hibernate 自动建表/更新（`spring.jpa.hibernate.ddl-auto`）。
> 本文档按当前实体代码（`blc/src/main/java/com/jiyu/pojo`）整理，方便文档同学快速查字段。

## 1) 表清单（总览）

| 表名 | 说明 | 主键 | 关键约束 / 关联 |
| --- | --- | --- | --- |
| `category` | 分类（影库/影单类型） | `id` | - |
| `user` | 用户（账号 + 资料） | `id` | `username` 唯一 |
| `movie` | 影库条目 | `id` | `cid -> category.id` |
| `collection` | 影单/收藏室条目（当前实现不按用户隔离） | `id` | `cid -> category.id` |
| `blog_article` | 影评文章 | `id` | - |
| `movie_comment` | 电影短评/评分 | `id` | `UK(user_id, movie_id)`；`movie_id -> movie.id` |
| `movie_comment_like` | 短评点赞 | `id` | `UK(user_id, comment_id)`；`comment_id -> movie_comment.id` |
| `user_favorite` | 用户收藏 | `id` | `UK(user_id, movie_id)`；`movie_id -> movie.id` |
| `user_movie_status` | 用户观影状态（想看/看过） | `id` | `UK(user_id, movie_id)`；`movie_id -> movie.id` |

> 说明：
> - `user_id` 相关外键在代码里使用了 `ConstraintMode.NO_CONSTRAINT`（即 **数据库不强制外键约束**，但逻辑上仍然关联 `user.id`）。
> - 列名包含 `user` / `cast` / `date` 等可能与关键字冲突的情况，写 SQL 时建议用反引号包起来。

## 2) 枚举字段（VARCHAR 存储）

| 表.字段 | 枚举 | 可选值 |
| --- | --- | --- |
| `movie_comment.status` | `MovieCommentStatus` | `PENDING` / `APPROVED` / `REJECTED` |
| `user_movie_status.status` | `MovieWatchStatus` | `WISH` / `WATCHED` |

## 3) 各表字段明细

### 3.1 `category`（分类）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | INT | 否 | AUTO_INCREMENT | PK | 主键 |
| `name` | VARCHAR(255) | 否 | - | - | 分类名称 |

### 3.2 `user`（用户）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | BIGINT | 否 | AUTO_INCREMENT | PK | 用户ID |
| `username` | VARCHAR(255) | 否 | - | UK(`uk_user_username`) | 登录名 |
| `password` | VARCHAR(255) | 是 | NULL | - | 密码（哈希） |
| `salt` | VARCHAR(255) | 是 | NULL | - | 密码盐 |
| `role` | VARCHAR(32) | 是 | `normal` | - | 角色（如 `admin`/`normal`） |
| `first_name` | VARCHAR(255) | 是 | NULL | - | 名 |
| `last_name` | VARCHAR(255) | 是 | NULL | - | 姓 |
| `email` | VARCHAR(255) | 是 | NULL | - | 邮箱 |
| `phone` | VARCHAR(255) | 是 | NULL | - | 电话 |
| `address` | VARCHAR(255) | 是 | NULL | - | 地址 |
| `city` | VARCHAR(255) | 是 | NULL | - | 城市 |
| `state` | VARCHAR(255) | 是 | NULL | - | 省/州 |
| `country` | VARCHAR(255) | 是 | NULL | - | 国家 |
| `bio` | VARCHAR(255) | 是 | NULL | - | 简介 |
| `profile_picture` | VARCHAR(255) | 是 | NULL | - | 头像（URL/路径） |

### 3.3 `movie`（影库条目）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | INT | 否 | AUTO_INCREMENT | PK | 主键 |
| `cid` | INT | 是 | NULL | FK(`fk_movie_category`)，IDX(`idx_movie_cid`) | 分类ID（`category.id`） |
| `cover` | VARCHAR(255) | 是 | NULL | - | 封面（URL/路径） |
| `title` | VARCHAR(255) | 是 | NULL | - | 片名 |
| `cast` | VARCHAR(255) | 是 | NULL | - | 演职员（字段名建议用反引号） |
| `date` | VARCHAR(255) | 是 | NULL | - | 上映日期（字段名建议用反引号） |
| `press` | VARCHAR(255) | 是 | NULL | - | 发行/出品方 |
| `summary` | VARCHAR(255) | 是 | NULL | - | 简介 |

### 3.4 `collection`（影单/收藏室条目）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | INT | 否 | AUTO_INCREMENT | PK | 主键 |
| `cid` | INT | 是 | NULL | FK(`fk_collection_category`)，IDX(`idx_collection_cid`) | 分类ID（`category.id`） |
| `cover` | VARCHAR(255) | 是 | NULL | - | 封面（URL/路径） |
| `title` | VARCHAR(255) | 是 | NULL | - | 片名 |
| `cast` | VARCHAR(255) | 是 | NULL | - | 演职员（字段名建议用反引号） |
| `date` | VARCHAR(255) | 是 | NULL | - | 上映日期（字段名建议用反引号） |
| `press` | VARCHAR(255) | 是 | NULL | - | 发行/出品方 |
| `summary` | VARCHAR(255) | 是 | NULL | - | 简介 |

> 注：当前 `collection` 表不区分用户（不是“某个用户的收藏”），如果后续要支持每个用户自己的影单，需要新增 `user_id` 等字段并调整业务逻辑。

### 3.5 `blog_article`（影评文章）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | INT | 否 | AUTO_INCREMENT | PK | 主键 |
| `article_title` | VARCHAR(255) | 是 | NULL | - | 标题 |
| `article_content_html` | LONGTEXT | 是 | NULL | - | 内容（HTML） |
| `article_content_md` | LONGTEXT | 是 | NULL | - | 内容（Markdown） |
| `article_abstract` | VARCHAR(255) | 是 | NULL | - | 摘要 |
| `article_cover` | VARCHAR(255) | 是 | NULL | - | 封面 |
| `article_date` | DATE | 是 | NULL | - | 发布日期 |

### 3.6 `movie_comment`（电影短评/评分）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | BIGINT | 否 | AUTO_INCREMENT | PK | 主键 |
| `user_id` | BIGINT | 否 | - | UK(`uk_movie_comment_user_movie`)，IDX(`idx_movie_comment_user_id`) | 用户ID（逻辑关联 `user.id`，DB 不强制外键） |
| `movie_id` | INT | 否 | - | UK(`uk_movie_comment_user_movie`)，FK(`fk_movie_comment_movie`)，IDX(`idx_movie_comment_movie_id`) | 电影ID（`movie.id`） |
| `rating` | INT | 是 | NULL | - | 评分（约束在服务端：1~5，可为空） |
| `content` | VARCHAR(512) | 是 | NULL | - | 短评内容（服务端默认最大长度配置为 280） |
| `status` | VARCHAR(16) | 否 | `APPROVED` | - | 审核状态（枚举） |
| `created_at` | DATETIME(6) | 否 | - | - | 创建时间 |
| `updated_at` | DATETIME(6) | 否 | - | - | 更新时间 |

### 3.7 `movie_comment_like`（短评点赞）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | BIGINT | 否 | AUTO_INCREMENT | PK | 主键 |
| `user_id` | BIGINT | 否 | - | UK(`uk_movie_comment_like_user_comment`)，IDX(`idx_movie_comment_like_user_id`) | 用户ID（逻辑关联 `user.id`，DB 不强制外键） |
| `comment_id` | BIGINT | 否 | - | UK(`uk_movie_comment_like_user_comment`)，FK(`fk_movie_comment_like_comment`)，IDX(`idx_movie_comment_like_comment_id`) | 短评ID（`movie_comment.id`） |
| `created_at` | DATETIME(6) | 否 | - | - | 点赞时间 |

### 3.8 `user_favorite`（用户收藏）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | BIGINT | 否 | AUTO_INCREMENT | PK | 主键 |
| `user_id` | BIGINT | 否 | - | UK(`uk_user_favorite_user_movie`)，IDX(`idx_user_favorite_user_id`) | 用户ID（逻辑关联 `user.id`，DB 不强制外键） |
| `movie_id` | INT | 否 | - | UK(`uk_user_favorite_user_movie`)，FK(`fk_user_favorite_movie`)，IDX(`idx_user_favorite_movie_id`) | 电影ID（`movie.id`） |
| `created_at` | DATETIME(6) | 否 | - | - | 收藏时间 |

### 3.9 `user_movie_status`（用户观影状态）

| 字段 | 类型 | NULL | 默认 | 约束/索引 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `id` | BIGINT | 否 | AUTO_INCREMENT | PK | 主键 |
| `user_id` | BIGINT | 否 | - | UK(`uk_user_movie_status_user_movie`)，IDX(`idx_user_movie_status_user_id`) | 用户ID（逻辑关联 `user.id`，DB 不强制外键） |
| `movie_id` | INT | 否 | - | UK(`uk_user_movie_status_user_movie`)，FK(`fk_user_movie_status_movie`)，IDX(`idx_user_movie_status_movie_id`) | 电影ID（`movie.id`） |
| `status` | VARCHAR(16) | 否 | - | - | 观影状态（枚举：`WISH`/`WATCHED`） |
| `marked_at` | DATETIME(6) | 否 | - | - | 标记时间 |
| `created_at` | DATETIME(6) | 否 | - | - | 创建时间 |
| `updated_at` | DATETIME(6) | 否 | - | - | 更新时间 |
