#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROP="$ROOT/local.properties"

if ! command -v gh >/dev/null 2>&1; then
  echo "Instala GitHub CLI (gh) y ejecuta: gh auth login"
  exit 1
fi

if [[ ! -f "$PROP" ]]; then
  echo "Crea $PROP desde local.properties.example"
  exit 1
fi

USER="$(gh api user -q .login)"
TOKEN="$(gh auth token)"
POM_URL="https://maven.pkg.github.com/ritherdieler/components/com/dscorp/components/1.0.1/components-1.0.1.pom"
HTTP_CODE="$(curl -s -o /dev/null -w '%{http_code}' -u "${USER}:${TOKEN}" "$POM_URL")"

if [[ "$HTTP_CODE" != "200" ]]; then
  echo "GitHub Packages respondió HTTP ${HTTP_CODE} (se requiere scope read:packages)."
  echo "Ejecuta en una terminal interactiva:"
  echo "  gh auth refresh --hostname github.com -s read:packages"
  echo "Completa el flujo en el navegador y vuelve a correr este script."
  exit 1
fi

python3 - "$PROP" "$USER" "$TOKEN" <<'PY'
import re
import sys

path, user, token = sys.argv[1:4]
with open(path, encoding="utf-8") as f:
    text = f.read()

def set_line(key: str, value: str, content: str) -> str:
    pattern = rf"^{re.escape(key)}=.*$"
    line = f"{key}={value}"
    if re.search(pattern, content, flags=re.M):
        return re.sub(pattern, line, content, count=1, flags=re.M)
    return content.rstrip() + "\n" + line + "\n"

text = set_line("gpr.user", user, text)
text = set_line("gpr.key", token, text)
with open(path, "w", encoding="utf-8") as f:
    f.write(text)
PY

echo "gpr.user y gpr.key actualizados en local.properties (token con acceso a Packages verificado)."
