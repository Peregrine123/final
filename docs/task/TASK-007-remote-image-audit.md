# TASK-007 网图引用排查与本地化（离线可用）

> 目标：系统性排查“通过网络 URL 引用的图片”（外网 / 本机 file-service），将所有图片下载/落地到本地资源（仓库内可控目录或后端自托管目录），并把所有出现位置与替换口径固化到文档，避免环境不稳定/断网导致图片不可用。

## 任务内容
- 要做什么：
  - 盘点仓库内所有“非本地文件引用”的图片来源（http/https）。
  - 统计数量（按唯一 URL、按出现次数、按文件维度），并列出每一个出现位置（文件 + 行号 + URL）。
  - 将所有 external（第三方域名）图片下载到本地，并替换引用为“本地可控来源”：
    - 前端 UI 静态图：落到前端 `assets`（随构建产物发布）。
    - 业务封面/头像等上传型资源：落到后端 `/api/file/**`（随部署初始化到上传目录）。
- 为什么要做（背景/问题）：
  - 外网图片/第三方域名存在失效、速度、合规与离线不可用风险。
  - 便于统一替换为本地静态资源（前端打包）或后端 `/api/file/**` 自托管资源，保证断网/弱网环境下图片仍可用。
- 目标（可量化）：
  - 形成一份“网图清单”文档：覆盖率 100%，可定位到文件与行号。
  - 给出当前基线统计：external（第三方域名）与 localhost `/api/file/` 分别有多少条唯一 URL、多少处引用。
  - 完成本地化后：external 图片引用清零（external unique = 0），所有图片在无外网条件下仍可展示。
- 范围（包含/不包含）：
  - 包含：`blc-vue/src/**`、`blc/*.sql`、`blc/seed-*.sql`、`docs/**` 中的图片 URL 引用。
  - 不包含：第三方依赖目录（如 `node_modules`）、构建产物与压缩 bundle 的“间接字符串噪声”（本次以源码/种子数据为准）。
- 影响面（模块/接口/数据/用户）：
  - 前端：页面背景/卡片图、电影占位封面。
  - 种子数据：书籍封面、文章封面/内容示例图（依赖 `/api/file/**` 或外网域名）。

## 具体实施方案
以“全仓库文本检索 + 规则筛选”的方式识别图片 URL（http/https），按以下口径判定为“网图引用”：
- 明确图片后缀：`.png/.jpg/.jpeg/.gif/.webp/.svg`（含常见 `?query`、`@h_1280` 等后缀变体）。
- 或者出现在明确图片上下文：`data-image=...`、CSS `background-image/url(...)`、Markdown `![](...)`、`<img ...>`、以及业务字段语义（cover/avatar/poster）。
- 额外：后端文件服务路径 `/api/file/` 视为“非仓库本地图片”（运行期依赖上传目录/静态映射）。

### 本地化落地策略（核心）
1. 前端静态 UI 图片（页面背景/装饰/示例卡片图）
   - 落地位置：前端 `assets` 目录（随前端构建发布）。
   - 替换方式：将模板中的外网 URL 改为引用本地资源（避免热链与 403/限流风险）。
2. 后端 `/api/file/**`（封面/头像/文章示例图等“上传型资源”）
   - 落地位置：后端上传目录（由 `${blc.upload.dir}` 指定），并提供“初始化资源集”随部署落地。
   - 替换方式：数据库/种子数据中使用相对路径（建议 `/api/file/<filename>`），避免写死 `http://localhost:8443` 造成跨环境不可用。

### 步骤
1. 扫描并收集所有疑似图片 URL（源码与种子数据为主），输出“文件-行号-URL”三元组。
2. 去重并统计：
   - 唯一 URL 数、出现次数。
   - 以域名/类别（external vs localhost `/api/file/`）聚合。
   - 以文件聚合，便于后续替换实施。
3. 以“清单”为输入执行本地化：
   - external 图片逐一下载并落地到约定目录（前端 assets 或后端上传目录初始化集）。
   - 更新所有引用点：external -> 本地引用（前端 assets 或 `/api/file/<filename>`）。
   - 清理“写死 host”的链接：`http://localhost:8443/api/file/...` -> `/api/file/...`（提升跨环境可移植性）。
4. 将统计、清单、落地口径与目标状态固化到本文档（作为 TASK-007 的交付物）。

## 过程验证
1. 验证点：清单覆盖率
   - 观察/指标：仓库中所有图片 URL 引用都能在清单中定位到文件与行号。
   - 通过标准：针对前端页面与种子 SQL 中的图片展示点，均能在清单中找到对应 URL。
2. 验证点：分类准确性
   - 观察/指标：external（第三方域名）与 localhost `/api/file/`（自托管/本地 file-service）分组合理，且数量可复算。
   - 通过标准：同一 URL 不重复计入“唯一 URL”；同一 URL 多处出现会体现在“出现次数/位置”中。
3. 验证点：离线可用
   - 观察/指标：在无外网条件下，前端页面与数据封面图仍能正常加载（不出现 404/跨域/长时间 pending）。
   - 通过标准：external 图片引用清零；所有图片均来自“前端 assets”或“后端 `/api/file/**`”。

## 最终结果验证
### 统计口径与基线（2026-01-16）
- 总引用处（occurrences）：45
- 唯一图片 URL（unique）：43
- 唯一 external（第三方域名）：26
- 唯一 localhost `/api/file/`：17
- 补充核对：后端已提交的前端静态产物目录 `blc/src/main/resources/static/` 中未发现 third-party 图片域名（以域名关键字检索核对）。
- 域名分布（按唯一 URL）：
  - `i.loli.net`：19
  - `localhost:8443`：17
  - `img2.baidu.com`：2
  - `b0.bdstatic.com`：1
  - `pic.rmb.bdstatic.com`：1
  - `wx1.sinaimg.cn`：1
  - `i.ibb.co`：1
  - `via.placeholder.com`：1

### 目标状态（本地化完成后）
- external 图片引用：0
- 数据中所有封面/示例图链接：统一使用本地可控来源（前端 assets 或 `/api/file/<filename>`）
- 环境要求：
  - 不依赖外网访问第三方域名也能正常展示图片。
 - 达成情况：已于 2026-01-16 完成（见“本地化实施落地”）。

### 清单（按文件聚合：文件:行号 -> URL）
#### 前端源码：external 图片
- `blc-vue/src/components/home/AppIndex.vue`
  - 148 -> https://img2.baidu.com/it/u=505381648,3692530915&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1222
  - 154 -> https://b0.bdstatic.com/5c7424e7410237293ed39e0f7ffcff96.jpg@h_1280
  - 160 -> https://wx1.sinaimg.cn/mw690/e979b63agy1hrx88dr5bvj22ld4cy7wh.jpg
  - 165 -> https://pic.rmb.bdstatic.com/bjh/240305/8b05f6f96dd82195b491cc29fc90f6636449.jpeg
  - 506 -> https://i.ibb.co/FDGqCmM/papers-co-ag74-interstellar-wide-space-film-movie-art-33-iphone6-wallpaper.jpg
  - 520 -> https://img2.baidu.com/it/u=1601820106,4160962638&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889
- `blc-vue/src/components/library/MovieDetails.vue`
  - 135 -> https://via.placeholder.com/300x450?text=No+Cover

#### 种子数据：external 图片（书籍封面）
- `blc/blc.sql`
  - 59 -> https://i.loli.net/2019/04/10/5cadaa0d0759b.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada7e73d601.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada824c7119.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada858e6019.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada87fd5c72.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada8986e13a.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada8b8a3a17.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada8e1aa892.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada9202d970.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada940e206a.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada962c287c.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada976927da.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada9870c2ab.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada99bd8ca5.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada9c852298.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada9d9d23a6.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cada9ec514c9.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cad63931ce27.jpg
  - 59 -> https://i.loli.net/2019/04/10/5cad643643d4c.jpg

### 本地文件现状核对（2026-01-16）
- 后端上传目录基线：仓库中已存在 `blc/uploads/`，并包含上述 19 个 `i.loli.net` 对应的同名文件（例如 `5cadaa0d0759b.jpg`、`5cad63931ce27.jpg` 等），同时覆盖本清单里出现的全部 `/api/file/<filename>` 文件名（例如 `default-cover.png`、`jhzp0p-2021-12-02.jpg` 等）。
- 结论：数据侧“外网封面”可通过“改链接指向 `/api/file/<同名文件>`”实现本地化，避免重复下载与重复存储。

### 本地化实施落地（2026-01-16，已完成）
- 前端外网图片落地：
  - 已新增本地资源：`blc-vue/src/assets/remote/`（`appindex-card1.jpg`、`appindex-card2.jpg`、`appindex-card3.jpg`、`appindex-card4.jpeg`、`appindex-poster1.jpg`、`appindex-poster2.jpg`、`no-cover.svg`）。
  - 已替换引用：
    - `blc-vue/src/components/home/AppIndex.vue`：卡片图与 CSS 背景图由 external URL 改为本地 `assets/remote`。
    - `blc-vue/src/components/library/MovieDetails.vue`：默认占位封面由 external 占位服务改为本地 `assets/remote/no-cover.svg`（避免外网依赖；原占位域名在部分环境下可能无法解析/访问）。
- external -> local 映射（关键点）：
  - AppIndex 卡片图：
    - `https://img2.baidu.com/...w=800&h=1222` -> `@/assets/remote/appindex-card1.jpg`
    - `https://b0.bdstatic.com/...jpg@h_1280` -> `@/assets/remote/appindex-card2.jpg`
    - `https://wx1.sinaimg.cn/...` -> `@/assets/remote/appindex-card3.jpg`
    - `https://pic.rmb.bdstatic.com/...jpeg` -> `@/assets/remote/appindex-card4.jpeg`
  - AppIndex CSS 背景图：
    - `https://i.ibb.co/...jpg` -> `~@/assets/remote/appindex-poster1.jpg`
    - `https://img2.baidu.com/...w=500&h=889` -> `~@/assets/remote/appindex-poster2.jpg`
  - MovieDetails 默认封面：
    - `https://via.placeholder.com/...` -> `@/assets/remote/no-cover.svg`
- 数据侧外网封面落地：
  - `blc/blc.sql`：`https://i.loli.net/2019/04/10/<filename>` -> `/api/file/<filename>`（对应文件已在 `blc/uploads/`）。
  - `blc/blc.sql` 与 `blc/seed-blog-2026.sql`：`http://localhost:8443/api/file/<filename>` -> `/api/file/<filename>`（避免写死 host，提升跨环境可移植性）。
  - `blc/seed-blog-2026.sql`：外部海报（`images.weserv.nl`）已本地化为 `/api/file/seed-lib-<hash>.<ext>`（对应文件已在 `blc/uploads/`）。
  - `blc/seed-library-2026.sql`：`https://images.weserv.nl/?url=upload.wikimedia.org/...` -> `/api/file/seed-lib-<hash>.<ext>`（海报已下载并落地到 `blc/uploads/`，保证影库/影单示例数据离线可用）。

#### 种子数据：localhost `/api/file/`（依赖后端上传目录/静态映射）
- `blc/blc.sql`
  - 37 -> http://localhost:8443/api/file/hi370y-2021-11-30.jpg
  - 37 -> http://localhost:8443/api/file/DdGBk1R3mj5er6v.png
  - 37 -> http://localhost:8443/api/file/4phmf7-2021-11-30.png
  - 37 -> http://localhost:8443/api/file/m8cjsh-2021-11-30.png
  - 37 -> http://localhost:8443/api/file/sq0cqw-2021-11-30.png
  - 59 -> http://localhost:8443/api/file/iqmpmm-2021-12-01.png
  - 59 -> http://localhost:8443/api/file/via0pk-2021-12-01.png
  - 59 -> http://localhost:8443/api/file/7gycoa-2021-12-01.png
  - 59 -> http://localhost:8443/api/file/hmo5hg-2021-12-01.png
  - 59 -> http://localhost:8443/api/file/l4v5lg-2021-12-01.png
  - 59 -> http://localhost:8443/api/file/87jv13-2021-12-01.jpg
- `blc/seed-blog-2026.sql`
  - 61 -> http://localhost:8443/api/file/jhzp0p-2021-12-02.jpg
  - 104 -> http://localhost:8443/api/file/qacakp-2021-12-02.jpg
  - 157 -> http://localhost:8443/api/file/5cada9c852298.jpg
  - 210 -> http://localhost:8443/api/file/5cada9202d970.jpg
  - 239 -> http://localhost:8443/api/file/default-cover.png
  - 279 -> http://localhost:8443/api/file/default-cover.png
  - 293 -> http://localhost:8443/api/file/default-cover.png
  - 340 -> http://localhost:8443/api/file/5cad63931ce27.jpg

## 风险与回滚
- 风险点：
  - external 图片域名可能失效/访问受限，导致 UI 资源加载失败。
  - 种子数据中的封面如果依赖外网，会在离线环境下出现“无封面”。
  - 版权/授权风险：把外网图片落到本地或自托管前，需要明确资源来源与授权。
  - 仓库体积增长：将图片纳入版本管理会增加仓库大小与 clone 成本。
- 规避手段：
  - 明确替换策略：优先使用本地 `assets` 或后端 `/api/file/`（上传目录可随环境初始化）。
  - 对“占位封面”统一走本地资源（避免依赖 placeholder 域名）。
  - 对图片资源做“必要集”控制：仅纳入实际引用到的图片；必要时引入大文件管理方案（例如 LFS）或改为部署侧下发。
- 回滚策略（描述思路即可）：
  - 若后续执行了本地化替换：可回退到替换前的引用（external URL 或原始 `/api/file` 形式），并删除新增的本地图片资源。

## 里程碑 / 截止时间
- 开始：2026-01-16
- 截止：2026-01-16

## 关联信息
- 对应 Issue：TBD
- 相关 PR：TBD
- 其他资料链接：TBD
