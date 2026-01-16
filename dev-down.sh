#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="${ROOT_DIR}/.run"

BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"

kill_by_pidfile() {
  local name="$1"
  local pid_file="$2"

  if [[ ! -f "${pid_file}" ]]; then
    echo "[dev-down] ${name}: pid file not found (${pid_file})"
    return 0
  fi

  local pid
  pid="$(cat "${pid_file}" || true)"
  if [[ -z "${pid}" ]]; then
    echo "[dev-down] ${name}: empty pid file (${pid_file})"
    rm -f "${pid_file}"
    return 0
  fi

  if ! kill -0 "${pid}" >/dev/null 2>&1; then
    echo "[dev-down] ${name}: not running (stale pid ${pid})"
    rm -f "${pid_file}"
    return 0
  fi

  echo "[dev-down] stopping ${name} (pid ${pid}) ..."
  kill "${pid}" >/dev/null 2>&1 || true

  # Graceful wait up to ~10s.
  for _ in {1..20}; do
    if ! kill -0 "${pid}" >/dev/null 2>&1; then
      rm -f "${pid_file}"
      echo "[dev-down] ${name}: stopped"
      return 0
    fi
    sleep 0.5
  done

  echo "[dev-down] ${name}: still running; sending SIGKILL"
  kill -9 "${pid}" >/dev/null 2>&1 || true
  rm -f "${pid_file}"
}

kill_by_pidfile "frontend" "${FRONTEND_PID_FILE}"
kill_by_pidfile "backend" "${BACKEND_PID_FILE}"

