# blc-ms-master

后端：Spring Boot（Maven）`blc/`  
前端：Vue2 + Element-UI `blc-vue/`

## 环境要求

- JDK 8
- Maven
- Node.js + npm（Vue CLI 4，建议 Node 14/16）
- MySQL 8（默认库名 `blc`）
- Redis（本项目 pom 引入了 redis starter；本地开发脚本会启动 redis-server）

## 快速启动（推荐）

1) 初始化依赖（首次需要）

```bash
cd blc-vue
npm install
```

2) 一键启动前后端（会尝试 `sudo service mysql start` / `sudo service redis-server start`）

```bash
./dev-up.sh
```

如果你本机 MySQL 账号/密码/端口与项目默认配置不同（默认 `blc/blc123456@127.0.0.1:3306/blc`），推荐不要改代码，直接创建本地覆盖文件：`.run/dev.env`（已在 `.gitignore` 里）。

示例：

```bash
cat > .run/dev.env <<'EOF'
# Spring Boot datasource override
SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/blc?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Optional: change frontend proxy target
# VUE_APP_API_TARGET=http://localhost:8443
EOF
```

WSL 注意：如果仓库在 `/mnt/<盘符>/...`（例如 `/mnt/d/...`），前端 dev server 可能会因为 9p 文件系统非常慢/卡住。建议把项目放到 WSL 的 Linux 文件系统目录（比如 `~/projects/...`）。本项目脚本会把 Maven 本地仓库、npm cache 放到 `.run/`，减少反复下载与写入开销。

访问：

- 前端：http://localhost:8080
- 后端：http://localhost:8443

停止：

```bash
./dev-down.sh
```

日志在：`.run/frontend.log`、`.run/backend.log`

## 手动启动（不使用脚本）

### 1) 启动并初始化数据库

后端默认配置见：`blc/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/blc?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
spring.datasource.username=blc
spring.datasource.password=blc123456
```

你可以按上述配置创建用户/库，或自行修改 `application.properties`。

可选：导入初始化数据（如需）

```bash
mysql -u root -p blc < blc/blc.sql
```

可选：补充影评示例文章（用于演示 Task-006 的博客样式）

```bash
MYSQL_PWD=blc123456 mysql -h 127.0.0.1 -P 3306 -u blc -D blc < blc/seed-blog-2026.sql
```

可选：替换影库/影单示例数据（把旧的“书籍风格”条目替换成电影条目 + 海报网图）

```bash
MYSQL_PWD=blc123456 mysql -h 127.0.0.1 -P 3306 -u blc -D blc < blc/seed-library-2026.sql
```

### 2) 启动后端

```bash
cd blc
mvn spring-boot:run
```

后端默认端口：`8443`

### 3) 启动前端

```bash
cd blc-vue
npm install
npm run serve
```

前端默认端口：`8080`。开发环境下 `/api/*` 会被代理到后端：

- 默认：`http://localhost:8443`
- 可通过环境变量覆盖：`VUE_APP_API_TARGET=http://<你的后端IP>:8443`

示例：

```bash
VUE_APP_API_TARGET=http://localhost:8443 npm run serve
```
