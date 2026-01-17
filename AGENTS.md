# Repository Guidelines

## Project Structure & Module Organization
- `blc/`: Spring Boot backend (Maven, Java 8). Main code lives in `blc/src/main/java/com/jiyu/` (layers: `controller/`, `service/`, `dao/`, `pojo/`, `config/`).
- `blc-vue/`: Vue 2 + Element-UI frontend. App code in `blc-vue/src/` (`components/`, `views/`, `router/`, `store/`).
- `docs/`: handover notes and task writeups.
- `dev-up.sh` / `dev-down.sh`: local dev helper scripts.
- `blc/src/main/resources/static/`: a built SPA snapshot served by the backend for demos; the editable frontend source is in `blc-vue/`.

## Build, Test, and Development Commands
- `./dev-up.sh`: best-effort starts MySQL/Redis, then runs backend + frontend; logs go to `.run/backend.log` and `.run/frontend.log`.
- `./dev-down.sh`: stops the dev processes started by `dev-up.sh`.
- Backend (manual): `cd blc && mvn spring-boot:run` (default `http://localhost:8443`).
- Backend tests: `cd blc && mvn test`.
- Frontend (manual): `cd blc-vue && npm ci && npm run serve` (default `http://localhost:8080`, proxies `/api/*` to the backend).
- Frontend build: `cd blc-vue && npm run build`.

## Coding Style & Naming Conventions
- Java: 4-space indentation; follow existing `com.jiyu.*` layering; classes `UpperCamelCase`, methods `lowerCamelCase`.
- Vue: 2-space indentation in `.vue` files; components in PascalCase (e.g., `MovieDetail.vue`).
- Avoid committing local artifacts: `.run/`, `blc/target/`, `blc-vue/node_modules/`, `blc-vue/dist/`.

## Testing Guidelines
- Backend uses JUnit 5 + Spring Boot Test/MockMvc; tests live under `blc/src/test/java/**` and typically end with `*Test.java`.
- Tests run with the `test` profile and an in-memory H2 DB (`blc/src/test/resources/application-test.properties`).
- For UI changes, include a short smoke-test note (login/register, library list/detail) in your PR.

## Commit & Pull Request Guidelines
- Commit messages follow Conventional Commits (examples from history: `feat(ui): ...`, `fix(auth): ...`, `docs: ...`); include a scope and a task id when relevant (e.g., `TASK-007`).
- PRs should include: what/why, how to verify, linked task/issue, screenshots for UI changes, and passing `mvn test`.

## Configuration Tips
- Put machine-specific overrides in `.run/dev.env` (examples: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `VUE_APP_API_TARGET`) and keep secrets out of git.
- Use `http://localhost:8080` in dev to match backend CORS/cookie expectations.

