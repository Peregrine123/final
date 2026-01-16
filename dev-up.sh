#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="${ROOT_DIR}/.run"

BACKEND_DIR="${ROOT_DIR}/blc"
FRONTEND_DIR="${ROOT_DIR}/blc-vue"

ENV_FILE="${RUN_DIR}/dev.env"

BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"
BACKEND_LOG_FILE="${RUN_DIR}/backend.log"
FRONTEND_LOG_FILE="${RUN_DIR}/frontend.log"

mkdir -p "${RUN_DIR}" "${RUN_DIR}/m2" "${RUN_DIR}/npm-cache"

if [[ -f "${ENV_FILE}" ]]; then
  # Allow per-developer overrides without touching tracked files.
  # Example: SPRING_DATASOURCE_USERNAME / SPRING_DATASOURCE_PASSWORD / SPRING_DATASOURCE_URL
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
  echo "[dev-up] loaded env overrides: ${ENV_FILE}"
fi

read_prop() {
  local file="$1"
  local key="$2"
  awk -F= -v key="${key}" '
    $0 ~ /^[[:space:]]*#/ { next }
    NF < 2 { next }
    {
      k=$1
      gsub(/^[[:space:]]+|[[:space:]]+$/, "", k)
      if (k != key) next
      v=$0
      sub(/^[^=]*=/, "", v)
      sub(/^[[:space:]]+/, "", v)
      print v
      exit
    }' "${file}" 2>/dev/null || true
}

can_connect_tcp() {
  local host="$1"
  local port="$2"

  if command -v nc >/dev/null 2>&1; then
    nc -z -w 1 "${host}" "${port}" >/dev/null 2>&1
    return $?
  fi
  if command -v timeout >/dev/null 2>&1; then
    timeout 1 bash -lc "cat < /dev/null > /dev/tcp/${host}/${port}" >/dev/null 2>&1
    return $?
  fi

  # Fallback: local-only port check (may miss Windows-hosted services in WSL).
  if command -v ss >/dev/null 2>&1; then
    ss -ltn "( sport = :${port} )" 2>/dev/null | tail -n +2 | grep -q LISTEN
    return $?
  fi
  return 1
}

is_port_listening() {
  local port="$1"
  if command -v ss >/dev/null 2>&1; then
    ss -ltn "( sport = :${port} )" 2>/dev/null | tail -n +2 | grep -q LISTEN
    return $?
  fi
  # Fallback: best-effort.
  return 1
}

pid_is_running() {
  local pid_file="$1"
  [[ -f "${pid_file}" ]] || return 1
  local pid
  pid="$(cat "${pid_file}" || true)"
  [[ -n "${pid}" ]] || return 1
  kill -0 "${pid}" >/dev/null 2>&1
}

parse_mysql_jdbc_url() {
  local url="$1"

  MYSQL_HOST=""
  MYSQL_PORT=""
  MYSQL_DB=""

  # jdbc:mysql://host[:port]/db?params
  if [[ "${url}" =~ ^jdbc:mysql://([^/:?]+)(:([0-9]+))?/([^?]+) ]]; then
    MYSQL_HOST="${BASH_REMATCH[1]}"
    MYSQL_PORT="${BASH_REMATCH[3]:-3306}"
    MYSQL_DB="${BASH_REMATCH[4]}"
    return 0
  fi
  return 1
}

db_preflight_check() {
  local app_props="${BACKEND_DIR}/src/main/resources/application.properties"

  # Spring Boot env overrides take precedence over application.properties.
  DS_URL="${SPRING_DATASOURCE_URL:-$(read_prop "${app_props}" "spring.datasource.url")}"
  DS_USER="${SPRING_DATASOURCE_USERNAME:-$(read_prop "${app_props}" "spring.datasource.username")}"
  DS_PASS="${SPRING_DATASOURCE_PASSWORD:-$(read_prop "${app_props}" "spring.datasource.password")}"
  BACKEND_PORT="${SERVER_PORT:-$(read_prop "${app_props}" "server.port")}"
  BACKEND_PORT="${BACKEND_PORT:-8443}"

  if [[ -z "${DS_URL}" || -z "${DS_USER}" ]]; then
    echo "[dev-up] warning: cannot read datasource config from ${app_props}; skipping db preflight."
    return 0
  fi

  if ! parse_mysql_jdbc_url "${DS_URL}"; then
    echo "[dev-up] warning: cannot parse spring.datasource.url (${DS_URL}); skipping db preflight."
    return 0
  fi

  echo "[dev-up] db: mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB} (user ${DS_USER})"

  if [[ "${BLC_SKIP_DB_CHECK:-0}" == "1" ]]; then
    echo "[dev-up] skipping db preflight (BLC_SKIP_DB_CHECK=1)."
    return 0
  fi

  if ! can_connect_tcp "${MYSQL_HOST}" "${MYSQL_PORT}"; then
    cat <<EOF
[dev-up] ERROR: cannot reach MySQL at ${MYSQL_HOST}:${MYSQL_PORT}.
[dev-up] - If you're on Windows/WSL: make sure MySQL is running and port ${MYSQL_PORT} is accessible from WSL.
[dev-up] - Or override the datasource via ${ENV_FILE}:
[dev-up]     SPRING_DATASOURCE_URL=jdbc:mysql://<host>:<port>/<db>?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
EOF
    return 1
  fi

  if ! command -v mysql >/dev/null 2>&1; then
    echo "[dev-up] warning: mysql client not found; cannot validate db credentials (will rely on backend log)."
    return 0
  fi

  local out=""
  out="$(MYSQL_PWD="${DS_PASS}" mysql --protocol=tcp -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${DS_USER}" -e "SELECT 1;" 2>&1)" || {
    if echo "${out}" | grep -qi "Access denied"; then
      cat <<EOF
[dev-up] ERROR: MySQL auth failed for user '${DS_USER}'.
[dev-up] ${out}

[dev-up] Fix options:
[dev-up] 1) Create matching user + database (run in MySQL as root/admin):
[dev-up]    CREATE DATABASE IF NOT EXISTS ${MYSQL_DB} DEFAULT CHARACTER SET utf8mb4;
[dev-up]    CREATE USER IF NOT EXISTS '${DS_USER}'@'localhost' IDENTIFIED BY '<password>';
[dev-up]    CREATE USER IF NOT EXISTS '${DS_USER}'@'127.0.0.1' IDENTIFIED BY '<password>';
[dev-up]    GRANT ALL PRIVILEGES ON ${MYSQL_DB}.* TO '${DS_USER}'@'localhost';
[dev-up]    GRANT ALL PRIVILEGES ON ${MYSQL_DB}.* TO '${DS_USER}'@'127.0.0.1';
[dev-up]    FLUSH PRIVILEGES;
[dev-up]
[dev-up] 2) Or override credentials by creating ${ENV_FILE} (recommended for teammates):
[dev-up]    SPRING_DATASOURCE_USERNAME=<your_user>
[dev-up]    SPRING_DATASOURCE_PASSWORD=<your_password>
[dev-up]    SPRING_DATASOURCE_URL=${DS_URL}
EOF
      return 1
    fi
    echo "[dev-up] ERROR: MySQL connection test failed:"
    echo "${out}"
    return 1
  }

  out="$(MYSQL_PWD="${DS_PASS}" mysql --protocol=tcp -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${DS_USER}" -D "${MYSQL_DB}" -e "SELECT 1;" 2>&1)" || {
    if echo "${out}" | grep -qi "Unknown database"; then
      cat <<EOF
[dev-up] ERROR: database '${MYSQL_DB}' does not exist.
[dev-up] ${out}

[dev-up] Fix options:
[dev-up] - Create database:  CREATE DATABASE IF NOT EXISTS ${MYSQL_DB} DEFAULT CHARACTER SET utf8mb4;
[dev-up] - Optional import: mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u ${DS_USER} -p ${MYSQL_DB} < blc/blc.sql
EOF
      return 1
    fi
    if echo "${out}" | grep -qi "Access denied"; then
      cat <<EOF
[dev-up] ERROR: user '${DS_USER}' has no access to database '${MYSQL_DB}'.
[dev-up] ${out}

[dev-up] Fix: GRANT ALL PRIVILEGES ON ${MYSQL_DB}.* TO '${DS_USER}'@'localhost'; (and/or '@127.0.0.1')
EOF
      return 1
    fi
    echo "[dev-up] ERROR: MySQL database test failed:"
    echo "${out}"
    return 1
  }

  echo "[dev-up] db preflight OK."
  return 0
}

wait_for_service() {
  local name="$1"
  local pid_file="$2"
  local host="$3"
  local port="$4"
  local log_file="$5"
  local timeout_secs="${6:-30}"

  local start_ts
  start_ts="$(date +%s)"

  while true; do
    if ! pid_is_running "${pid_file}"; then
      echo "[dev-up] ${name} exited during startup."
      rm -f "${pid_file}" || true
      if [[ -f "${log_file}" ]]; then
        echo "[dev-up] last 80 lines of ${log_file}:"
        tail -n 80 "${log_file}" || true
      fi
      return 1
    fi

    if can_connect_tcp "${host}" "${port}"; then
      echo "[dev-up] ${name} is up (${host}:${port})."
      return 0
    fi

    if (( "$(date +%s)" - start_ts >= timeout_secs )); then
      echo "[dev-up] ${name} still starting; check log: ${log_file}"
      return 0
    fi

    sleep 0.5
  done
}

try_start_service() {
  local name="$1"
  shift

  # Never block on sudo password in an automation script.
  if command -v sudo >/dev/null 2>&1 && sudo -n true >/dev/null 2>&1; then
    # shellcheck disable=SC2068
    sudo "$@" >/dev/null 2>&1 || true
    echo "[dev-up] tried: ${name} -> sudo $*"
  else
    echo "[dev-up] ${name} not running and sudo is not passwordless; please start it manually."
  fi
}

echo "[dev-up] repo: ${ROOT_DIR}"

# Basic tooling sanity checks (fail fast with clear message).
if ! command -v mvn >/dev/null 2>&1; then
  echo "[dev-up] missing required command: mvn (install Maven / configure PATH)"
  exit 1
fi
if ! command -v npm >/dev/null 2>&1; then
  echo "[dev-up] missing required command: npm (install Node.js + npm / configure PATH)"
  exit 1
fi

# 1) Ensure MySQL/Redis are up (best-effort).
if ! is_port_listening 3306; then
  if command -v service >/dev/null 2>&1; then
    try_start_service "mysql" service mysql start
  elif command -v systemctl >/dev/null 2>&1; then
    try_start_service "mysql" systemctl start mysql
  else
    echo "[dev-up] mysql not listening on 3306; cannot auto-start (no service/systemctl)."
  fi
fi

if ! is_port_listening 6379; then
  if command -v service >/dev/null 2>&1; then
    try_start_service "redis-server" service redis-server start
  elif command -v systemctl >/dev/null 2>&1; then
    try_start_service "redis-server" systemctl start redis-server
  else
    echo "[dev-up] redis not listening on 6379; cannot auto-start (no service/systemctl)."
  fi
fi

# 1.5) Fail fast with actionable message if DB config is invalid.
db_preflight_check

# 2) Start backend.
if pid_is_running "${BACKEND_PID_FILE}"; then
  echo "[dev-up] backend already running (pid $(cat "${BACKEND_PID_FILE}"))."
else
  mkdir -p "${BACKEND_DIR}/uploads"
  echo "[dev-up] starting backend (Spring Boot) ..."
  nohup bash -lc "cd \"${BACKEND_DIR}\" && exec mvn -Dmaven.repo.local=\"${RUN_DIR}/m2\" -DskipTests spring-boot:run" \
    >"${BACKEND_LOG_FILE}" 2>&1 &
  echo $! > "${BACKEND_PID_FILE}"
  echo "[dev-up] backend pid: $(cat "${BACKEND_PID_FILE}") (log: ${BACKEND_LOG_FILE})"
  wait_for_service "backend" "${BACKEND_PID_FILE}" "127.0.0.1" "${BACKEND_PORT:-8443}" "${BACKEND_LOG_FILE}" 30
fi

# 3) Start frontend.
if pid_is_running "${FRONTEND_PID_FILE}"; then
  echo "[dev-up] frontend already running (pid $(cat "${FRONTEND_PID_FILE}"))."
else
  if [[ ! -d "${FRONTEND_DIR}/node_modules" ]]; then
    echo "[dev-up] frontend deps not found; installing (npm ci) ..."
    (cd "${FRONTEND_DIR}" && npm_config_cache="${RUN_DIR}/npm-cache" npm ci)
  fi
  echo "[dev-up] starting frontend (Vue dev server) ..."
  nohup bash -lc "cd \"${FRONTEND_DIR}\" && export BROWSER=none && export npm_config_cache=\"${RUN_DIR}/npm-cache\" && export VUE_APP_API_TARGET=\"${VUE_APP_API_TARGET:-http://localhost:${BACKEND_PORT:-8443}}\" && exec npm run serve" \
    >"${FRONTEND_LOG_FILE}" 2>&1 &
  echo $! > "${FRONTEND_PID_FILE}"
  echo "[dev-up] frontend pid: $(cat "${FRONTEND_PID_FILE}") (log: ${FRONTEND_LOG_FILE})"
  wait_for_service "frontend" "${FRONTEND_PID_FILE}" "127.0.0.1" "8080" "${FRONTEND_LOG_FILE}" 30
fi

echo "[dev-up] urls:"
echo "  - frontend: http://localhost:8080"
echo "  - backend : http://localhost:${BACKEND_PORT:-8443}"
echo "[dev-up] stop: ./dev-down.sh"
