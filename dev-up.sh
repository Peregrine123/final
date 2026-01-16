#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="${ROOT_DIR}/.run"

BACKEND_DIR="${ROOT_DIR}/blc"
FRONTEND_DIR="${ROOT_DIR}/blc-vue"

BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"
BACKEND_LOG_FILE="${RUN_DIR}/backend.log"
FRONTEND_LOG_FILE="${RUN_DIR}/frontend.log"

mkdir -p "${RUN_DIR}" "${RUN_DIR}/m2" "${RUN_DIR}/npm-cache"

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
  nohup bash -lc "cd \"${FRONTEND_DIR}\" && export BROWSER=none && export npm_config_cache=\"${RUN_DIR}/npm-cache\" && export VUE_APP_API_TARGET=\"http://localhost:8443\" && exec npm run serve" \
    >"${FRONTEND_LOG_FILE}" 2>&1 &
  echo $! > "${FRONTEND_PID_FILE}"
  echo "[dev-up] frontend pid: $(cat "${FRONTEND_PID_FILE}") (log: ${FRONTEND_LOG_FILE})"
fi

echo "[dev-up] urls:"
echo "  - frontend: http://localhost:8080"
echo "  - backend : http://localhost:8443"
echo "[dev-up] stop: ./dev-down.sh"
