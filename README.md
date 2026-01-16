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

