#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="$ROOT_DIR/.run"

mkdir -p "$RUN_DIR" "$RUN_DIR/m2" "$RUN_DIR/npm-cache"

# Start services (safe to run repeatedly).
sudo service mysql start >/dev/null 2>&1 || true
sudo service redis-server start >/dev/null 2>&1 || true

echo "Starting backend..."
if [[ -f "$RUN_DIR/backend.pid" ]] && kill -0 "$(cat "$RUN_DIR/backend.pid")" 2>/dev/null; then
  echo "  backend already running (pid $(cat "$RUN_DIR/backend.pid"))"
else
  : > "$RUN_DIR/backend.log"
  (
    cd "$ROOT_DIR/blc"
    nohup mvn -Dmaven.repo.local="$RUN_DIR/m2" -DskipTests spring-boot:run >> "$RUN_DIR/backend.log" 2>&1 &
    echo $! > "$RUN_DIR/backend.pid"
  )
  echo "  backend started (pid $(cat "$RUN_DIR/backend.pid"))"
fi

echo "Starting frontend..."
if [[ -f "$RUN_DIR/frontend.pid" ]] && kill -0 "$(cat "$RUN_DIR/frontend.pid")" 2>/dev/null; then
  echo "  frontend already running (pid $(cat "$RUN_DIR/frontend.pid"))"
else
  : > "$RUN_DIR/frontend.log"
  (
    cd "$ROOT_DIR/blc-vue"
    # Run vue-cli-service directly so the PID we track is the actual dev server (npm can leave child processes behind).
    nohup env npm_config_cache="$RUN_DIR/npm-cache" node ./node_modules/@vue/cli-service/bin/vue-cli-service.js serve --host 127.0.0.1 --port 8080 >> "$RUN_DIR/frontend.log" 2>&1 &
    echo $! > "$RUN_DIR/frontend.pid"
  )
  echo "  frontend started (pid $(cat "$RUN_DIR/frontend.pid"))"
fi

cat <<EOF

URLs:
  Frontend: http://localhost:8080
  Backend:  http://localhost:8443

Logs:
  $RUN_DIR/frontend.log
  $RUN_DIR/backend.log
EOF
