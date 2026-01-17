#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="${ROOT_DIR}/.run"

BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"

list_children() {
  local pid="$1"
  if command -v pgrep >/dev/null 2>&1; then
    pgrep -P "${pid}" 2>/dev/null || true
    return 0
  fi
  # Fallback when pgrep is unavailable.
  ps -o pid= --ppid "${pid}" 2>/dev/null || true
}

# Print the process tree PIDs in post-order (children first, then parent).
list_tree_pids() {
  local pid="$1"
  local child
  for child in $(list_children "${pid}"); do
    list_tree_pids "${child}"
  done
  echo "${pid}"
}

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
  mapfile -t tree_pids < <(list_tree_pids "${pid}")

  # Send SIGTERM to the whole tree to avoid leaving orphan processes behind
  # (e.g. npm -> node/vue-cli-service).
  for p in "${tree_pids[@]}"; do
    kill "${p}" >/dev/null 2>&1 || true
  done

  # Graceful wait up to ~10s.
  for _ in {1..20}; do
    local any_alive=0
    for p in "${tree_pids[@]}"; do
      if kill -0 "${p}" >/dev/null 2>&1; then
        any_alive=1
        break
      fi
    done
    if [[ "${any_alive}" -eq 0 ]]; then
      rm -f "${pid_file}"
      echo "[dev-down] ${name}: stopped"
      return 0
    fi
    sleep 0.5
  done

  echo "[dev-down] ${name}: still running; sending SIGKILL"
  for p in "${tree_pids[@]}"; do
    kill -9 "${p}" >/dev/null 2>&1 || true
  done
  rm -f "${pid_file}"
}

kill_by_pidfile "frontend" "${FRONTEND_PID_FILE}"
kill_by_pidfile "backend" "${BACKEND_PID_FILE}"
