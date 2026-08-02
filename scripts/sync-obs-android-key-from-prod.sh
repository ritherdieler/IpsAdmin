#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOCAL_PROPS="$ROOT/local.properties"
BACKEND_DEPLOY_CONFIG="$ROOT/../ispadmin-backend/scripts/deploy.config.local"
REMOTE_ENV="/opt/gigafiber/.env"

if [[ ! -f "$BACKEND_DEPLOY_CONFIG" ]]; then
  echo "No se encontro $BACKEND_DEPLOY_CONFIG" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$BACKEND_DEPLOY_CONFIG"

if [[ -z "${DEPLOY_SSH_PASSWORD:-}" && -z "${SSH_IDENTITY_FILE:-}" ]]; then
  echo "Configura DEPLOY_SSH_PASSWORD o SSH_IDENTITY_FILE en deploy.config.local" >&2
  exit 1
fi

SSH_OPTS=(-o StrictHostKeyChecking=no -p "${VPS_PORT:-22}")
if [[ -n "${SSH_IDENTITY_FILE:-}" ]]; then
  SSH_OPTS+=(-i "$SSH_IDENTITY_FILE")
fi

fetch_key() {
  if [[ -n "${DEPLOY_SSH_PASSWORD:-}" ]]; then
    SSHPASS="$DEPLOY_SSH_PASSWORD" sshpass -e ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" \
      "grep '^OBS_API_KEY_ANDROID=' '$REMOTE_ENV' | cut -d= -f2- | tr -d '\"' | tr -d ' '"
  else
    ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" \
      "grep '^OBS_API_KEY_ANDROID=' '$REMOTE_ENV' | cut -d= -f2- | tr -d '\"' | tr -d ' '"
  fi
}

KEY="$(fetch_key)"
if [[ -z "$KEY" ]]; then
  echo "OBS_API_KEY_ANDROID vacia en $REMOTE_ENV del servidor" >&2
  exit 1
fi

touch "$LOCAL_PROPS"
if grep -q '^OBS_API_KEY_ANDROID=' "$LOCAL_PROPS"; then
  if [[ "$(uname)" == "Darwin" ]]; then
    sed -i '' "s|^OBS_API_KEY_ANDROID=.*|OBS_API_KEY_ANDROID=$KEY|" "$LOCAL_PROPS"
  else
    sed -i "s|^OBS_API_KEY_ANDROID=.*|OBS_API_KEY_ANDROID=$KEY|" "$LOCAL_PROPS"
  fi
else
  printf '\nOBS_API_KEY_ANDROID=%s\n' "$KEY" >> "$LOCAL_PROPS"
fi

echo "OBS_API_KEY_ANDROID actualizada en local.properties (longitud ${#KEY})."
