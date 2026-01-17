# 功能实现清单（对照需求）

生成日期：2026-01-17

说明：
- `[x]` 已实现（或基本可用）
- `[~] 部分实现` 已实现核心流程，但存在缺项/仅前端限制/仅演示占位
- `[ ] 未实现`

---

## 用户侧

- `[~] 部分实现` 用户注册
  - 已有：注册页面（`blc-vue/src/views/Register.vue`），后端注册接口 `/api/register` + 用户名占用校验 `/api/register/check`（`blc/src/main/java/com/jiyu/controller/LoginController.java`）。
  - 已有：密码加盐哈希存储（MD5 + salt + 2 次迭代）。
  - 未有：邮箱唯一性校验（后端目前只校验 `username` 是否重复）。

- `[~] 部分实现` 用户登录
  - 已有：登录页面（`blc-vue/src/views/Login.vue`），后端登录接口 `/api/login` + 用户名存在校验 `/api/login/check`。
  - 未有：登录失败次数限制、防暴力破解；双因素认证（2FA）；“忘记密码/重置密码”流程。
  - 未有：邮箱 + 密码登录（当前主要按 `username` 登录）。

- `[~] 部分实现` 编辑个人资料
  - 已有：个人资料展示/编辑页面（`blc-vue/src/components/userProfile/*`），后端用户接口 `/api/users/*`（`blc/src/main/java/com/jiyu/controller/UserController.java`）。
  - 已有：修改密码时会重新加盐哈希存储（`blc/src/main/java/com/jiyu/service/UserService.java`）。
  - 未有：后端级别的“只能修改/查看本人资料”的访问控制（目前按是否登录拦截，未细分到用户本人）。
  - 未有：“实时保存修改”（当前为点击保存提交）。

- `[~] 部分实现` 浏览电影列表
  - 已有：电影列表页（`blc-vue/src/components/library/MoviesDefault.vue`），分类侧边栏（`blc-vue/src/components/library/SideMenu.vue`）。
  - 已有：后端电影列表 `/api/movies`、按分类 `/api/categories/{cid}/movies`（`blc/src/main/java/com/jiyu/controller/LibraryController.java`）。
  - 已有：分页（前端本地分页 slice；后端未做分页）。
  - 未有：基于历史/偏好的个性化推荐。

- `[~] 部分实现` 搜索电影
  - 已有：关键词搜索接口 `/api/search?keywords=...`（按 `title/cast` 模糊匹配），前端搜索栏联动列表（`blc-vue/src/components/library/SearchBar.vue`）。
  - 未有：按类型/导演等多维度搜索；智能提示/自动补全。

- `[~] 部分实现` 查看电影详情
  - 已有：详情页（`blc-vue/src/components/library/MovieDetails.vue`），展示封面/简介/主演(导演)/上映日期等。
  - 说明：当前详情页通过拉取 `/api/movies` 后前端按 `id` 过滤得到详情（后端暂无 `/api/movies/{id}` 单条查询接口）。
  - 未有：视频预览等多媒体能力（短评/评分等模块目前为 UI 演示 mock 数据）。

- `[ ] 未实现` 观看电影（在线播放/离线下载）

- `[ ] 未实现` 评论和评分（含点赞/回复、审核机制）

- `[~] 部分实现` 收藏电影
  - 已有：收藏页/添加收藏页（`blc-vue/src/components/library/CollectionManagement.vue`、`AddCollectionManagement.vue` 等），后端接口 `/api/collections`、`/api/user/add`、`/api/user/delete`。
  - 未有：按“用户维度”的收藏（当前 `collection` 数据未绑定 `userId`/`username`，更像全局收藏表，无法区分不同用户）。

---

## 管理员侧

- `[~] 部分实现` 博客（文章）发布与管理
  - 已有：文章列表/详情/编辑器/管理（`blc-vue/src/components/Blog/*`），后端接口 `/api/article/*`、`/api/admin/article`、`/api/admin/article/{id}`（`blc/src/main/java/com/jiyu/controller/BlogController.java`）。
  - 已有：文章分页（后端 `BlogPage` 分页返回）。
  - 未有：标签分类、草稿、定时发布等内容管理能力。

- `[ ] 未实现` 管理用户账户（封禁、投诉处理、普通管理员/超级管理员权限区分等）

- `[~] 部分实现` 电影管理（添加/编辑/移除）
  - 已有：管理页（`blc-vue/src/components/library/LibraryManagement.vue` + `EditForm.vue`），后端接口 `/api/admin/movies`、`/api/admin/delete`。
  - 未有：批量导入/导出；后端强制的管理员权限校验（目前主要是前端按 `role` 控制显示）。

- `[ ] 未实现` 生成报表（活跃度/偏好等统计分析与可视化）

- `[~] 部分实现` 影评管理
  - 已有：当前“影评管理”页面实际管理的是博客文章（`ArticleManagement.vue` 对应后端 `BlogController`）。
  - 未有：对“用户评论/评分”的审核、批量操作等管理能力（因为用户评论/评分功能未落地）。

