#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="$ROOT_DIR/.run"

stop_pidfile() {
  local name="$1"
  local pidfile="$RUN_DIR/$2"

  if [[ ! -f "$pidfile" ]]; then
    echo "  $name: pidfile not found ($pidfile)"
    return 0
  fi

  local pid
  pid="$(cat "$pidfile" || true)"
  if [[ -z "${pid:-}" ]]; then
    echo "  $name: empty pidfile ($pidfile)"
    return 0
  fi

  if ! kill -0 "$pid" 2>/dev/null; then
    echo "  $name: not running (pid $pid)"
    return 0
  fi

  echo "  stopping $name (pid $pid)..."
  kill "$pid" 2>/dev/null || true

  for _ in $(seq 1 30); do
    if ! kill -0 "$pid" 2>/dev/null; then
      echo "  $name: stopped"
      return 0
    fi
    sleep 1
  done

  echo "  $name: force killing (pid $pid)..."
  kill -9 "$pid" 2>/dev/null || true
}

echo "Stopping frontend/backend..."
stop_pidfile "frontend" "frontend.pid"
stop_pidfile "backend (mvn)" "backend.pid"

# In case the Vue dev server was started via npm previously, it may leave the
# underlying vue-cli-service process running even after stopping the npm PID.
pgrep -f "blc-vue/node_modules/.*/@vue/cli-service/bin/vue-cli-service\\.js serve" | xargs -r kill 2>/dev/null || true

# In case Spring Boot devtools forked a child JVM, stop it too.
pgrep -f "com\\.jiyu\\.BlcApplication" | xargs -r kill 2>/dev/null || true

echo "Done."
