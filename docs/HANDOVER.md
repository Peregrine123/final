# blc-ms-master 仓库接手指南（后端 `blc` + 前端 `blc-vue`）

> 目标：帮你在最短时间内“能跑起来/能定位代码/知道风险点/知道下一阶段怎么推进”。本文基于仓库现状梳理，尽量不做猜测；凡是存在不一致或潜在坑，会明确标注。

---

## 0. 5 分钟上手（先把项目跑起来）

### 0.1 仓库里有什么

- `blc/`：后端（Spring Boot 2.5.6，Java 8，Spring Data JPA，Shiro，Druid，MySQL）。
- `blc-vue/`：前端（Vue CLI 4，Vue 2.7.x，Vue Router history，Vuex，Element-UI，axios，mavon-editor，gsap）。

### 0.2 端口与访问入口（默认）

- 后端端口：`8443`（配置在 `blc/src/main/resources/application.properties`）
- 前端开发端口：`8080`（配置在 `blc-vue/vue.config.js`）
- API 前缀：大部分为 `/api/...`
- Druid 监控：`http://localhost:8443/druid/`（用户名 `admin` / 密码 `123456`，见后端 `DruidConfig`）
- 静态文件/前端页面：后端自带一份构建后的 SPA 静态资源在 `blc/src/main/resources/static/`（可直接由后端提供页面）

### 0.3 推荐的本地开发方式（前后端分离）

0) 一键启动（推荐，Ubuntu/WSL）：
- 在仓库根目录执行 `./dev-up.sh`（会尝试启动 MySQL/Redis，并启动后端+前端；日志在 `.run/backend.log` / `.run/frontend.log`）
- 停止：`./dev-down.sh`

1) 启动后端（需要你本机安装环境）：
- JDK：建议 `8`（`pom.xml` 中 `java.version=1.8`）
- Maven：3.6+（仓库带了 `.mvn/wrapper` 但缺少 `mvnw` 脚本，见下文“构建方式”）
- MySQL：8.x
- Redis：可选（当前 `pom.xml` 带了 `spring-boot-starter-data-redis`，但业务未强依赖）

2) 启动前端：
- Node：建议用 `16 LTS`（Vue CLI 4 生态更稳；你可用 `nvm use 16`）
- 在 `blc-vue/` 下执行 `npm install` / `npm run serve`（如需显式绑定可用：`npm run serve -- --host 127.0.0.1 --port 8080`）
- 前端 axios 默认指向 `http://localhost:8443/api`（见 `blc-vue/src/main.js`）
- 后端 CORS 默认只放行 `http://localhost:8080` 且允许携带 cookie（见 `MyWebConfigurer`）；因此请用 `http://localhost:8080` 访问前端（不要用 `127.0.0.1:8080`）
- 登录/权限：
  - 先去 `/register` 注册一个账号即可登录
  - 若要看到“管理中心”的管理入口，需要把该用户在数据库里设置为管理员：`UPDATE user SET role='admin' WHERE username='你的用户名';`

### 0.4 后端数据库配置（你大概率需要改的第一处配置）

文件：`blc/src/main/resources/application.properties`

仓库已提供一套“本地开发默认” datasource（db=`blc`，user=`blc`，pass=`blc123456`）；如果你本机数据库账号/密码不同，按需修改：

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/blc?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
spring.datasource.username=blc
spring.datasource.password=blc123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# local dev: 自动补齐缺表/缺列（movie/collection/user 扩展字段等）
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

初始化本地数据库（Ubuntu/WSL 示例）：

```bash
sudo service mysql start
sudo mysql -e "CREATE DATABASE IF NOT EXISTS blc DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "CREATE USER IF NOT EXISTS 'blc'@'localhost' IDENTIFIED BY 'blc123456';"
sudo mysql -e "GRANT ALL PRIVILEGES ON blc.* TO 'blc'@'localhost'; FLUSH PRIVILEGES;"
sudo mysql blc < blc/blc.sql
```

> 重要：`blc/blc.sql` 与当前实体不完全一致（见“数据库与实体”）；仓库默认用 `ddl-auto=update` 来帮助你本地快速跑通，但不要把它当作生产方案。

### 0.5 前端静态资源由谁提供？（两种模式）

- **模式 A：前后端分离（开发/联调常用）**
  - 前端：`npm run serve`（`localhost:8080`）
  - 后端：`localhost:8443`
  - 依赖 CORS + cookie
- **模式 B：后端托管前端静态资源（部署/演示常用）**
  - 后端的 `blc/src/main/resources/static/` 已包含一份构建产物（`index.html/css/js`）
  - 后端通过 `ErrorConfig` 处理 history 刷新
  - 注意：如果你重新构建前端，通常需要把 `blc-vue/dist/` 覆盖/同步到 `blc/src/main/resources/static/`

---

## 1. 仓库结构速览（你接手时最常用的定位路径）

```
blc-ms-master/
  blc/                 # 后端 Spring Boot
    pom.xml
    blc.sql            # 初始化 SQL（注意：与当前实体不完全一致，见“数据库与实体”）
    src/main/java/com/jiyu/
      BlcApplication.java
      controller/      # 对外 API（登录/博客/影单/用户资料）
      service/         # 业务层（JPA DAO 封装）
      dao/             # Spring Data JPA Repository
      pojo/            # JPA Entity（对应数据库表）
      config/          # Web/CORS/静态资源/Druid/Shiro
      interceptor/     # 登录拦截（基于 Shiro Subject）
      realm/           # Shiro Realm（认证）
      result/          # 统一返回体（部分接口使用，部分未使用）
    src/main/resources/
      application.properties
      log4j.properties
      static/          # 已构建的前端静态资源（index.html + css/js/img）
  blc-vue/             # 前端 Vue（Vue2.7 + ElementUI）
    package.json
    vue.config.js
    src/
      main.js          # axios 基址、路由守卫
      router/index.js  # 路由（含 requireAuth meta）
      store/index.js   # Vuex（localStorage 持久化 username）
      views/           # Login / Register / Home（壳）
      components/
        common/        # NavMenu 等
        Blog/          # 影评/博客：列表、详情、编辑、管理
        library/       # 影单/电影：列表、分类、管理、收藏
        userProfile/   # 个人资料：查看/编辑
    static/            # 前端本地静态图标（like/collection 等）
    dist/              # 前端构建产物（已存在一份）
```

---

## 2. 技术栈与构建方式（按模块拆）

### 2.1 后端 `blc`（Spring Boot）

**关键依赖（见 `blc/pom.xml`）**

- Spring Boot：`2.5.6`
- Web：`spring-boot-starter-web`
- ORM：`spring-boot-starter-data-jpa`
- 数据库：MySQL `mysql-connector-java 8.0.21`
- 认证：`org.apache.shiro:shiro-spring 1.3.2`
- 连接池/监控：`com.alibaba:druid 1.2.6`（附带 /druid 监控）
- 日志：`log4j 1.2.17`（注意：非常老，安全风险高，建议升级，见“风险点”）
- 其他：`lombok`、`commons-lang 2.6`

**构建与运行**

- Maven 项目：`blc/pom.xml`
- 仓库有 `.mvn/wrapper/*`，但缺少常见的 `mvnw` / `mvnw.cmd` 脚本；因此默认还是按“本机装 Maven”来跑。

**入口类**

- `blc/src/main/java/com/jiyu/BlcApplication.java`

### 2.2 前端 `blc-vue`（Vue2.7 + Vue CLI 4）

**关键依赖（见 `blc-vue/package.json`）**

- Vue：`^2.6.11`（当前仓库 node_modules 实际解析为 `2.7.16`，支持 Composition API）
- UI：Element UI `2.15.6`
- 状态：Vuex `3.6.2`
- 路由：Vue Router `3.2.0`（history 模式）
- HTTP：axios `0.24.0`
- Markdown 编辑器：mavon-editor `2.10.0`
- 动画：gsap `3.12.5`
- 构建：Vue CLI 4（`@vue/cli-service ~4.5.0`）

**开发服务器**

- `localhost:8080`（见 `blc-vue/vue.config.js`）

---

## 3. 后端：模块职责、关键实现与扩展点

### 3.1 Web 配置：跨域、拦截、静态资源、SPA 刷新

文件：`blc/src/main/java/com/jiyu/config/MyWebConfigurer.java`

- **登录拦截**：对所有路径 `/**` 启用 `LoginInterceptor`，但排除：
  - `/api/login`、`/api/register`、`/api/logout`
  - `/api/covers`（上传封面）
  - `/api/file/**`（静态文件：封面/头像等）
  - `/api/register/check`、`/api/login/check`
  - `/index.html`
- **CORS**：仅允许来源 `http://localhost:8080`，允许 cookie，方法 `POST/GET/PUT/OPTIONS/DELETE`。
- **文件静态映射**：`/api/file/**` -> 本地目录 `${blc.upload.dir}`（默认 `${user.dir}/uploads`，本项目用 `dev-up.sh` 启动时实际落在 `blc/uploads/`）。

文件：`blc/src/main/java/com/jiyu/error/ErrorConfig.java`

- **SPA 路由刷新支持**：404 时转发到 `/index.html`，用于 Vue Router history 模式下的深链接刷新。

### 3.2 认证/鉴权：Shiro + 自定义拦截器

文件：`blc/src/main/java/com/jiyu/config/ShiroConfiguration.java`

- 使用 `WJRealm` 做认证（密码 md5 + 2 次迭代）。
- **注意**：`ShiroFilterFactoryBean` 没有配置 `filterChainDefinitionMap`（即“哪些路径需要什么 filter”），项目实际上依赖 `LoginInterceptor` 在 Spring MVC 层做拦截。
- `rememberMeManager()` 定义了但未注入 `securityManager`，`rememberMe` 可能并未生效（需要你实际验证）。

文件：`blc/src/main/java/com/jiyu/realm/WJRealm.java`

- 根据用户名查 `User`，取 `password` 与 `salt` 返回 `SimpleAuthenticationInfo`。
- **潜在坑**：如果用户名不存在，`userService.getByUserName` 返回 `null`，会在 `user.getPassword()` 处 NPE；当前 `LoginController` 里虽有 `/api/login/check`，但真正 `/api/login` 并未先做 check（前端做了 blur 校验，但不能假设永远执行）。

文件：`blc/src/main/java/com/jiyu/interceptor/LoginInterceptor.java`

- 核心判断：`Subject` 未 `isAuthenticated` 且未 `isRemembered` 就拦截（直接 `return false`，但未返回统一错误体/状态码）。

### 3.3 数据访问：Spring Data JPA（DAO + Entity）

文件：`blc/src/main/java/com/jiyu/dao/*`

- 全部是 `JpaRepository`，靠方法名生成查询：
  - `UserDAO.findByUsername`
  - `MovieDAO.findAllByCategory` / `findAllByTitleLikeOrCastLike`
  - `CollectionDAO.findAllByCategory` / `findAllByTitleLikeOrCastLike`
  - `BlogArticleDAO.findById(int id)`（注意：这里自定义了一个 `findById` 返回实体，不是 Optional）

文件：`blc/src/main/java/com/jiyu/pojo/*`

- `User`、`Category`、`Movie`、`Collection`、`BlogArticle` 都是 `@Entity`。
- `Movie`/`Collection` 与 `Category` 是 `@ManyToOne`，前端提交对象时需要带 `category: { id: '...' }`。

### 3.4 业务层：Service（基本是 DAO 封装）

文件：`blc/src/main/java/com/jiyu/service/*`

- `UserService.update`：如果传了新密码会重新生成盐并加密；不传密码则保留旧密码/盐。
- `MovieService`/`CollectionService`：分页/搜索/分类查询，全部靠 JPA。
- `BlogArticleService.list`：用 `PageRequest` + `Sort` 做分页。

---

## 4. 后端 API 清单（以代码为准）

> 注意：接口返回风格不统一：登录/注册/文章保存等使用 `Result{code,message,result}`，但 `/api/movies`、`/api/users/*` 等直接返回实体 JSON。

### 4.1 登录与注册（`LoginController`）

- `GET /api/login/check?username=...`：检查用户名是否存在（只校验用户名）
  - 成功：`{ code:200, message:"成功", result:"<username>" }`
  - 失败：`{ code:400, message:"用户名不存在", result:null }`
- `POST /api/login`：登录（Shiro）
  - body：`{ "username": "...", "password": "..." }`
  - 成功：`Result`，`result` 为用户名
- `GET /api/register/check?username=...`：检查用户名是否可用
- `POST /api/register`：注册（后端生成盐 + md5*2）
  - body：`{ "username":"...", "password":"..." }`（前端虽有更多字段，但目前注册只提交这两个）
  - 默认写入：`role="normal"`
- `GET /api/user/role?username=...`：返回用户角色（`admin` / `normal`）
- `GET /api/logout`：登出
- `GET /api/authentication`：用于前端路由守卫的“探活/认证成功”接口（返回纯字符串）

### 4.2 影评/博客（`BlogController`）

- `POST /api/admin/article`：保存/更新文章（后端会写 `articleDate=今天`）
- `GET /api/article/{size}/{page}`：分页列表（返回 `Page`）
- `GET /api/article/{id}`：文章详情
- `DELETE /api/admin/article/{id}`：删除文章

### 4.3 电影/影单与收藏（`LibraryController`）

- `GET /api/movies`：电影列表（按 id 倒序）
- `POST /api/admin/movies`：新增/更新电影
- `POST /api/admin/delete`：删除电影（body `{id:...}`）
- `GET /api/categories/{cid}/movies`：按分类过滤电影（`cid=0` 表示全部）
- `GET /api/search?keywords=...`：按 title/cast 模糊搜索
- `POST /api/covers`：上传封面图（保存到 `${blc.upload.dir}`，返回可访问 URL：`http://<host>:8443/api/file/<filename>`）
- `GET /api/collections`：收藏列表（当前实现未按用户区分，是全量收藏表）
- `POST /api/user/add`：新增/更新收藏
- `POST /api/user/delete`：删除收藏（body `{id:...}`）
- `GET /api/categories/{cid}/collections`：按分类过滤收藏（`cid=0` 表示全部）

### 4.4 用户资料（`UserController`）

`/api/users` 资源风格接口（**返回的是实体 JSON，不是 Result 包装**）

- `GET /api/users/{id}`
- `GET /api/users/current?username=...`
- `POST /api/users`
- `PUT /api/users/{id}`（支持改密码：前端传 `password` 为新密码；不传则保留旧密码）
- `DELETE /api/users/{id}`

---

## 5. 数据库与实体：当前最需要你核对的一块

### 5.1 `blc/blc.sql` 与当前代码不一致（重点）

仓库提供的 `blc/blc.sql` 只包含：

- `blog_article`
- `book`
- `category`
- `user`

但当前后端实体是：

- `blog_article`（匹配度较高）
- `movie`（代码使用 `@Table(name="movie")`，SQL 里没有）
- `collection`（代码使用 `@Table(name="collection")`，SQL 里没有）
- `user`（代码字段比 SQL 多很多：first_name/last_name/email/.../profile_picture）
- `category`（代码使用自增主键，SQL 里 `id` 不是自增）

这意味着：

- **直接执行 `blc.sql` 不能完整跑起当前代码**（至少缺 `movie/collection` 表和 user 扩展列）。
- 你接手后第一件大事通常是：明确“当前线上库到底是什么结构”，然后补齐迁移脚本（建议 Flyway/Liquibase）。

### 5.2 快速验证/补齐 schema 的两条路（建议你选其一）

- **路 1：让 Hibernate 自动建表（仅用于快速本地验证）**
  - 仓库当前默认 `spring.jpa.hibernate.ddl-auto=update`（并使用 `MySQL8Dialect`），启动一次后端即可把缺表/缺列补出来
  - 如果你不希望本地自动改库，把它改回 `none`
  - 风险：自动建表的字段类型/索引/约束未必符合生产要求，不建议长期依赖
- **路 2：手写 DDL/迁移脚本（推荐，适合进入下一阶段开发）**
  - 以 `pojo` 实体为准补齐 `movie`、`collection`、`user` 扩展列等
  - 用 Flyway/Liquibase 把这些 DDL 固化为可回放的迁移

如果你只是想“页面先有数据能看”，注意 `blc.sql` 并没有 `movie/collection` 的数据（即使表补齐了也可能是空表）。你可以临时把 `book` 表的数据拷一份做演示：

```sql
INSERT INTO movie (cover,title,`cast`,`date`,press,summary,cid)
SELECT cover,title,author,`date`,press,`abs`,cid
FROM book
WHERE cid IS NOT NULL
LIMIT 30;

INSERT INTO collection (cover,title,`cast`,`date`,press,summary,cid)
SELECT cover,title,author,`date`,press,`abs`,cid
FROM book
WHERE cid IS NOT NULL
LIMIT 12;
```

### 5.2 代码侧实体（用于你对照数据库）

- `User`：`id, username, password, salt, role, firstName, lastName, email, phone, address, city, state, country, bio, profilePicture`
- `Category`：`id, name`
- `Movie`：`id, category(cid), cover, title, cast, date, press, summary`
- `Collection`：`id, category(cid), cover, title, cast, date, press, summary`
- `BlogArticle`：`id, articleTitle, articleContentHtml, articleContentMd, articleAbstract, articleCover, articleDate`

### 5.3 分类（Category）目前是“前端写死 + 数据库约定”

前端分类菜单在这些文件里写死了 index（即 cid）：

- `blc-vue/src/components/library/SideMenu.vue`
- `blc-vue/src/components/library/WorkMenu.vue`
- `blc-vue/src/components/library/EditForm.vue`（这里的分类与 SideMenu 的“名字/编号”存在不一致）

后端没有“获取分类列表”的接口；因此你必须保证：

- 数据库 `category.id` 与前端 hardcode 的编号一致
- 以及分类名称在前后端语义一致（否则会出现“点了动作却查出来科幻”等错乱）

### 5.4 （参考）按当前实体推导的 MySQL DDL 草案

> 这份 DDL 只用于你快速落地本地环境/对照线上库；字段长度、索引、外键策略请以你们团队规范为准。

```sql
-- category（建议自增，与实体 @GeneratedValue 对齐）
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- user（实体字段比 blc.sql 多很多）
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) DEFAULT NULL,
  `salt` VARCHAR(255) DEFAULT NULL,
  `role` VARCHAR(32) DEFAULT 'normal',
  `first_name` VARCHAR(255) DEFAULT NULL,
  `last_name` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(255) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `city` VARCHAR(255) DEFAULT NULL,
  `state` VARCHAR(255) DEFAULT NULL,
  `country` VARCHAR(255) DEFAULT NULL,
  `bio` VARCHAR(255) DEFAULT NULL,
  `profile_picture` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- movie（注意：`cast`/`date` 这类字段名建议用反引号包起来）
CREATE TABLE IF NOT EXISTS `movie` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cid` INT DEFAULT NULL,
  `cover` VARCHAR(255) DEFAULT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `cast` VARCHAR(255) DEFAULT NULL,
  `date` VARCHAR(255) DEFAULT NULL,
  `press` VARCHAR(255) DEFAULT NULL,
  `summary` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_movie_cid` (`cid`),
  CONSTRAINT `fk_movie_category` FOREIGN KEY (`cid`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- collection（当前实现未按用户隔离收藏；如果要做“每个用户自己的收藏”，表结构需要补 user_id）
CREATE TABLE IF NOT EXISTS `collection` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cid` INT DEFAULT NULL,
  `cover` VARCHAR(255) DEFAULT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `cast` VARCHAR(255) DEFAULT NULL,
  `date` VARCHAR(255) DEFAULT NULL,
  `press` VARCHAR(255) DEFAULT NULL,
  `summary` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_collection_cid` (`cid`),
  CONSTRAINT `fk_collection_category` FOREIGN KEY (`cid`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 6. 前端：模块职责、路由、状态与 API 调用约定

### 6.1 Axios 配置与请求策略

文件：`blc-vue/src/main.js`

- `axios.defaults.baseURL = 'http://localhost:8443/api'`
- `axios.defaults.withCredentials = true`（依赖后端 session/rememberMe cookie）
- 路由守卫：
  - 路由 `meta.requireAuth=true` 时，先检查 `store.state.user.username !== ''`
  - 再 `GET /authentication`（实际请求到 `/api/authentication`）来确认服务端会话是否有效

### 6.2 Vuex：仅存 username（localStorage 持久化）

文件：`blc-vue/src/store/index.js`

- `state.user.username` 从 `localStorage.getItem('user')` 初始化
- `login` mutation 会写入 localStorage
- `logout` mutation 会清理 localStorage

### 6.3 路由（history 模式）

文件：`blc-vue/src/router/index.js`

核心路由（简化版）：

- `/login`、`/register`
- `/home`（壳，包含 `NavMenu` + 子路由）
  - `/index`（首页 `AppIndex`）
  - `/library`（影单）
  - `/blog`（影评列表）
  - `/blog/article?id=...`（影评详情）
  - `/admin/editor`（写影评）
  - `/admin/ArticleManagement`（影评管理）
  - `/admin/libraryManagement`（电影管理）
  - `/user-profile`、`/edit-user-profile/:id`（资料查看/编辑）
  - `/collectionManagement`、`/addCollectionManagement`（收藏室）
  - `/MovieDetails/:id`（电影详情）

后端通过 `ErrorConfig` 把 404 转给 `/index.html`，以支持 history 路由刷新。

### 6.4 主要页面/组件与后端 API 对应

- 登录/注册：`src/views/Login.vue` / `src/views/Register.vue`
  - `/login/check`、`/login`
  - `/register/check`、`/register`
- 顶部菜单与角色：`src/components/common/NavMenu.vue`
  - `/user/role`（用来决定是否显示 admin 菜单）
  - `/logout`
- 影评：`src/components/Blog/*`
  - 列表分页：`/article/{size}/{page}`
  - 详情：`/article/{id}`
  - 管理：`/admin/article`（保存）、`/admin/article/{id}`（删除）
- 电影/影单：`src/components/library/*`
  - 列表：`/movies`
  - 分类：`categories/{cid}/movies`
  - 搜索：`/search?keywords=...`
  - 管理：`/admin/movies`、`/admin/delete`
  - 上传封面：组件 `ImgUpload` 直接 `action="http://localhost:8443/api/covers"`
- 用户资料：`src/components/userProfile/*`
  - 当前用户：`/users/current?username=...`
  - 详情：`/users/{id}`
  - 更新：`PUT /users/{id}`
- 收藏室：`src/components/library/*Collection*`
  - 列表：`/collections`
  - 新增：`/user/add`
  - 删除：`/user/delete`

---

## 7. 已知风险点 / 可能的坑（建议你接手后优先验证）

### 7.1 数据库与代码不一致（高优先级）

- `blc.sql` 缺少 `movie/collection` 表、`user` 扩展字段、`category` 自增策略不同。
- 如果你要进入下一阶段开发，建议先把“本地一键初始化数据库”补齐（迁移脚本/DDL）。

### 7.2 Windows 路径硬编码（高优先级）

- 上传封面保存路径已改为配置项：`blc.upload.dir`（默认 `${user.dir}/uploads`）。
- 部署时仍需关注“持久化/挂载策略”（容器/云主机上别写到临时目录）。

### 7.3 安全性（建议尽快处理）

- `log4j 1.2.17`：非常老，存在大量公开漏洞与维护风险；建议升级到 log4j2 或改用 `spring-boot-starter-logging` 默认方案。
- Druid 监控默认弱口令：`/druid` 的 `admin/123456` 应在生产禁用或至少改为强密码并加访问控制。
- 密码哈希：`md5 + 2`（不推荐），建议改用 `bcrypt/argon2`（需要配套迁移策略）。

### 7.4 认证链路的健壮性

- `WJRealm` 对不存在用户可能 NPE。
- `LoginInterceptor` 拦截失败时没有返回统一 JSON 或 401，前端可能表现为“请求无响应/控制台报错但 UI 不提示”。

### 7.5 前端分类写死且存在编号/名称不一致

- `SideMenu` 与 `EditForm` 的分类列表不一致；数据库 `category` 的 id/name 也可能与之一致也可能不一致，需要你对照线上库确认。

---

## 8. 下一阶段开发建议（行动清单）

### 8.1 必做（建议按顺序）

1) **明确真实的数据库结构**
   - 拉一份线上库的 schema（或导出 DDL），与 `pojo` 对照
   - 决定以“代码为准”还是“库为准”，把差异收敛
2) **补齐数据库初始化/迁移方案**
   - 最少：提供一份能跑通当前功能的 DDL + 初始化数据
   - 更推荐：引入 Flyway/Liquibase 并补迁移脚本
3) **把文件上传路径配置化**
   - 已通过 `blc.upload.dir` 配置化；下一步是补齐部署时的目录挂载/权限策略
4) **把登录拦截的失败返回统一为 401/Result**
   - 前端才能稳定地做“未登录跳转/提示”

### 8.2 建议做（提升可维护性）

- 统一所有 API 返回体：要么都用 `Result`，要么都 REST（建议都 REST + 统一异常处理）。
- 增加“分类列表”接口，前端不要写死分类。
- 增加最小化的集成测试：至少覆盖登录/文章分页/电影列表。
- 清理未使用依赖（redis、thymeleaf、jsp）或补齐其真实用途，降低维护噪音。

### 8.3 你可以先问团队/产品的 6 个关键问题

1) 线上部署形态：前后端同域（后端静态资源）还是分离部署（nginx + API）？
2) 数据库：线上是否已经有 `movie/collection` 表？字段与实体是否一致？
3) 认证：是否依赖 Shiro session？是否要求 token 化（JWT）？
4) 角色权限：目前只有 `admin/normal`，下一阶段是否要细化？
5) 文件存储：封面图片是否要上对象存储（OSS/MinIO）？
6) 未来功能：仓库名含 “ms”，是否计划拆微服务，还是保持单体？

---

## 9. 附：本仓库的“事实清单”（便于你快速核对）

- 后端端口：`server.port=8443`
- 前端开发端口：`8080`
- 前端 axios baseURL：`http://localhost:8443/api`
- 前端路由模式：history
- 后端 404 -> `/index.html`（解决 history 刷新）
- 文件上传目录：`${blc.upload.dir}`（默认 `${user.dir}/uploads`，本项目用 `dev-up.sh` 启动时实际落在 `blc/uploads/`）
- 文件访问前缀：`/api/file/<filename>`
- Druid 监控：`/druid/*`（默认账号密码写在代码里）
