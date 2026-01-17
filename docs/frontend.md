# 6.3. 前端实现

本节在原有写法基础上，结合当前项目的实际实现（Vue2 + Vue Router + Vuex + Element-UI + Axios + GSAP + mavon-editor），对前端项目结构、路由配置与组件开发进行补充与更新说明。

## 6.3.1. 项目结构

整体结构如图6-1所示：`public/` 中存放页面入口与静态图标；`src/` 为核心代码目录，包含入口文件、路由、状态管理、组件与页面；`vue.config.js` 提供开发环境代理与端口配置；`dist/` 为构建产物（在演示环境下可被拷贝/同步到后端 `blc/src/main/resources/static/` 进行一体化部署）。

**图6-1 前端项目结构（示意）**

```text
blc-vue/src
├── assets
│   ├── home-bg/              首页背景图资源
│   ├── remote/               首页卡片/占位图等资源
│   └── bg*.jpg/logo.png...   登录/注册背景、站点Logo等
├── components
│   ├── Blog/                 影评（博客）模块组件
│   │   └── article/          文章详情页子组件（Hero/正文/目录/侧栏）
│   ├── common/               通用组件（导航栏、全局加载遮罩）
│   ├── content/              内容组件（图片上传）
│   ├── home/                 首页组件
│   ├── library/              影库/影单/电影详情/收藏等组件
│   ├── userProfile/          用户资料相关组件
│   └── Card.vue              首页3D卡片组件
├── router/index.js           路由配置（history 模式）
├── store/index.js            Vuex：登录态持久化 + 全局 loading 状态
├── styles/markdown.css       Markdown 渲染样式（文章详情页）
├── views/                    页面级组件（Home/Login/Register）
├── App.vue                   根组件（挂载全局 Loading + router-view）
└── main.js                   入口：ElementUI/axios/路由守卫/拦截器
```

与旧版描述相比，当前版本在工程层面主要补充了：

1) **全局加载与过渡**：新增 `components/common/AppLoading.vue`，由 Vuex 的 `isLoading` 控制，配合路由钩子与 GSAP 实现更丝滑的页面切换体验。  
2) **博客详情页拆分**：将文章详情渲染拆分为 `BlogHero/BlogContent/BlogTOC/BlogSidebar` 等子组件，支持目录（TOC）、代码块复制、表格自适应等阅读增强。  
3) **电影详情页能力增强**：在 `MovieDetails.vue` 内增加“收藏 / 想看 / 看过 / 短评与评分 / 点赞 / 相关推荐”等一系列用户行为能力，形成完整交互闭环。  
4) **接口访问统一前缀**：Axios 统一以 `/api` 为 baseURL，开发环境通过 `vue.config.js` 代理到后端，减少环境差异与跨域问题。

## 6.3.2. 路由配置

项目使用 Vue Router 进行路由管理，采用 `history` 模式，并通过 `meta.requireAuth` 标记需要登录的页面（影单、博客列表、管理端、个人中心等）。

**Code 6-1 引入组件（节选）**

```js
import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home'

import AppIndex from '../components/home/AppIndex'
import Login from '../views/Login'
import Register from '../views/Register'

import LibraryIndex from '../components/library/LibraryIndex'
import MovieDetails from '@/components/library/MovieDetails'
import LibraryManagement from '../components/library/LibraryManagement'

import Articles from '../components/Blog/Articles'
import ArticleDetails from '../components/Blog/ArticleDetails'
import ArticleEditor from '../components/Blog/ArticleEditor'
import ArticleManagement from '../components/Blog/ArticleManagement'

import UserProfile from '../components/userProfile/UserProfile'
import EditUserProfile from '../components/userProfile/EditUserProfile'
import CollectionManagement from '../components/library/CollectionManagement'
import AddCollectionManagement from '../components/library/AddCollectionManagement'

Vue.use(VueRouter)
```

**Code 6-2 部分路由配置（节选）**

```js
const routes = [
  { path: '/', redirect: '/index', component: AppIndex, meta: { requireAuth: true } },
  {
    path: '/home',
    component: Home,
    redirect: '/index',
    children: [
      { path: '/index', component: AppIndex },
      { path: '/library', component: LibraryIndex, meta: { requireAuth: true } },
      { path: '/MovieDetails/:id', component: MovieDetails, props: true, meta: { requireAuth: true } },
      { path: '/blog', component: Articles, meta: { requireAuth: true } },
      { path: '/blog/article', component: ArticleDetails },
      { path: '/admin/editor', component: ArticleEditor, meta: { requireAuth: true } },
      { path: '/admin/ArticleManagement', component: ArticleManagement, meta: { requireAuth: true } },
      { path: '/admin/libraryManagement', component: LibraryManagement, meta: { requireAuth: true } },
      { path: '/user-profile', component: UserProfile, meta: { requireAuth: true } },
      { path: '/edit-user-profile/:id', component: EditUserProfile, meta: { requireAuth: true } },
      { path: '/collectionManagement', component: CollectionManagement, meta: { requireAuth: true } },
      { path: '/addCollectionManagement', component: AddCollectionManagement, meta: { requireAuth: true } },
    ],
  },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
]
```

为保证登录态与权限访问的正确性，项目在 `main.js` 中实现了路由守卫：当目标路由包含 `meta.requireAuth` 时，先检查本地持久化用户信息，再向后端发起 `/authentication` 会话校验；未登录或会话失效时跳转到登录页，并通过 `redirect` 参数记录原目标地址，便于登录后回跳。

**Code 6-3 路由守卫与会话校验（节选）**

```js
router.beforeEach((to, from, next) => {
  store.dispatch('startLoading')
  if (to.meta.requireAuth) {
    if (store.state.user.username !== '') {
      axios.get('/authentication').then(() => next()).catch(() => {
        store.commit('logout')
        next({ path: '/login', query: { redirect: to.fullPath } })
      })
    } else {
      next({ path: '/login', query: { redirect: to.fullPath } })
    }
  } else {
    next()
  }
})

router.afterEach(() => store.dispatch('finishLoading'))
```

## 6.3.3. 组件开发

前端组件结构如图6-2所示，按业务域划分为 Blog（影评/博客）、Library（影库与电影详情）、UserProfile（用户资料）及 common/content 等通用组件。各组件通过 Axios 调用后端 REST API 完成数据获取与提交，UI 层主要使用 Element-UI 组件（表格、表单、分页、对话框、加载指示等）实现快速搭建与一致性体验。

**图6-2 前端组件结构（示意）**

### 1) Blog 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `Articles.vue` | 影评列表组件 | 从后端分页获取文章列表（`GET /api/article/{pageSize}/{page}`），使用 CSS Grid 进行响应式卡片布局；封面缺失时用渐变占位；点击卡片跳转详情页（query 传参 `id`）。 |
| `ArticleDetails.vue` | 影评详情组件 | 获取文章详情（`GET /api/article/{id}`），页面拆分为 `BlogHero/BlogContent/BlogTOC/BlogSidebar`；正文使用 `v-html` 渲染后端生成的 HTML，同时在 `BlogContent` 中对代码块/表格/标题做二次增强（复制按钮、表格滚动、目录锚点）。 |
| `ArticleEditor.vue` | 编辑影评组件（管理员） | 基于 `mavon-editor` 提供 Markdown 编辑与预览；保存时提交 md 与 html（`POST /api/admin/article`）；摘要与封面在弹窗中编辑，封面支持 URL 或上传（复用 `ImgUpload.vue`）。 |
| `ArticleManagement.vue` | 管理影评组件（管理员） | 表格展示文章列表并支持查看/编辑/删除；编辑通过路由 `params` 传递文章对象到编辑器；删除调用 `DELETE /api/admin/article/{id}`。 |
| `BlogContent.vue` 等 | 详情页子组件 | `BlogContent` 负责渲染与增强；`BlogTOC` 根据解析出的标题渲染目录并支持锚点跳转；`BlogHero` 提供文章封面与标题区；`BlogSidebar` 作为侧栏容器。 |

### 2) Common 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `NavMenu.vue` | 全局导航菜单 | 顶部粘性导航栏；根据 `localStorage` 登录态与后端校验结果展示“登录/注册”或用户下拉菜单；管理员角色（`/api/user/role`）可见影评/电影管理入口；注销调用 `/api/logout` 并清理 Vuex。 |
| `AppLoading.vue` | 全局加载遮罩 | 监听 Vuex 的 `isLoading`，在路由切换时显示全屏遮罩；进入/离开动画由 GSAP 驱动，缓解白屏与跳变。 |
| `Card.vue` | 首页3D卡片 | 通过鼠标位移计算 `rotateX/rotateY` 与背景平移，实现 3D Hover 交互；支持 slot 传入标题与简介。 |

### 3) Content 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `ImgUpload.vue` | 图片上传组件 | 基于 Element-UI `el-upload`，上传到 `/api/covers`（携带 cookie：`with-credentials`）；后端返回 URL 字符串后回填到表单；用于电影封面与影评封面上传。 |

### 4) Home 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `AppIndex.vue` | 应用首页组件 | 首页背景轮播与文案淡入由 GSAP 控制；使用本地 `assets/home-bg` 图片避免热链失效；展示精选卡片区（复用 `Card.vue`）并提供博客/影单入口。 |

### 5) Library 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `LibraryIndex.vue` | 影单入口容器 | 左侧分类菜单（`SideMenu.vue`）+ 右侧电影列表（`MoviesDefault.vue`）；分类切换触发 `GET /api/categories/{cid}/movies` 并刷新列表数据。 |
| `MoviesDefault.vue` | 电影列表组件（新版） | 通过 `GET /api/movies` 拉取电影列表；采用 CSS Columns 形成瀑布流式海报墙并自适应断点；支持搜索栏（`/api/search?keywords=`）与前端分页。 |
| `MovieDetails.vue` | 电影详情组件（增强版） | 详情页包含：收藏（`PUT/DELETE /api/me/favorites/{movieId}`）、观影状态（`PUT /api/me/movie-status/{movieId}`）、评分汇总（`GET /api/movies/{movieId}/rating-summary`）、我的短评（`GET/PUT /api/me/movie-comments/{movieId}`）、短评列表（`GET /api/movies/{movieId}/comments?page=&size=`）与点赞（`PUT/DELETE /api/me/movie-comment-likes/{commentId}`）；管理员可一键跳转编辑器生成“长影评”初稿。 |
| `LibraryManagement.vue` + `EditForm.vue` | 电影管理组件（管理员） | 表格展示电影条目并支持新增/编辑/删除；编辑表单支持封面上传（复用 `ImgUpload.vue`）；提交保存 `POST /api/admin/movies`，删除调用 `POST /api/admin/delete`。 |
| `CollectionManagement.vue` | 个人观影行为页 | 以 Tab 形式展示“我的收藏/想看/看过”，分别调用 `/api/me/favorites` 与 `/api/me/movie-status?status=WISH/WATCHED`；布局复用海报瀑布流，支持分页。 |
| `AddCollectionManagement.vue` 等 | 收藏添加/旧版收藏室 | 提供在列表中快速收藏/取消收藏的交互（确认弹窗 + 图标状态切换）；用于演示“收藏室/影单”管理的扩展思路。 |

### 6) UserProfile 组件

| 组件 | 功能描述 | 开发细节 |
| --- | --- | --- |
| `UserProfile.vue` | 用户资料展示组件 | 通过 `/api/users/current?username=` 获取当前用户资料并展示；对未设置字段以“暂未设置”占位；提供跳转编辑按钮。 |
| `EditUserProfile.vue` | 编辑用户资料组件 | 通过 `/api/users/{id}` 获取与保存资料；支持更新基础信息与修改密码（前端二次确认）；保存成功后回到个人资料页。 |

## 6.3.4. 状态管理与前后端交互（补充）

1) **Vuex 登录态持久化**：`store/index.js` 将 `user.username` 持久化到 `localStorage`，页面刷新后仍能恢复；注销时清理本地缓存并重置状态。  
2) **Axios 统一配置**：在 `main.js` 中设置 `axios.defaults.baseURL = '/api'`，并开启 `withCredentials` 以适配后端基于 Cookie/Session 的登录态；开发环境通过 `vue.config.js` 将 `/api/*` 代理到后端 `http://localhost:8443`。  
3) **401 统一处理**：响应拦截器捕获 401 时自动触发 `logout` 并跳转登录页，避免各业务页面重复处理鉴权失败逻辑。  

## 6.3.5. 构建与部署（补充）

前端基于 Vue CLI 4 构建，开发/部署与后端联调的关键点如下：

1) **开发环境代理**：本项目统一使用相对路径 `/api` 访问后端接口，开发环境通过 `vue.config.js` 将 `/api/*` 代理到后端服务（默认 `http://localhost:8443`，也可通过环境变量覆盖）。  
2) **一体化部署**：执行 `npm run build` 生成静态资源到 `blc-vue/dist/`，随后可将构建产物部署到 Nginx，或同步到后端 `blc/src/main/resources/static/` 由 Spring Boot 统一对外提供页面与接口。  
3) **history 模式兼容**：路由采用 `history` 模式，生产环境需要保证刷新/直接访问子路由时不会 404（通常通过 Web 服务器 rewrite 或后端 fallback 返回 `index.html` 解决）。  

**Code 6-4 开发代理配置（节选）**

```js
// vue.config.js
proxy: {
  '/api': {
    target: process.env.VUE_APP_API_TARGET || 'http://localhost:8443',
    changeOrigin: true,
  },
},
```
