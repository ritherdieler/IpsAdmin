#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
GRADLE_FILE="$ROOT/presentation/build.gradle"
APK="$ROOT/presentation/build/outputs/apk/prod/debug/presentation-prod-debug.apk"
FIREBASE_APP_ID="1:74615302027:android:87751a6bad0a78cbb0318e"
FIREBASE_PROJECT="ispadmin-687ca"
TESTER_GROUP="gigafiber"

EXTRA_NOTES="${1:-}"

if [[ ! -f "$GRADLE_FILE" ]]; then
  echo "No se encontro $GRADLE_FILE" >&2
  exit 1
fi

read -r CURRENT_CODE CURRENT_NAME < <(
  python3 - "$GRADLE_FILE" <<'PY'
import re, sys
text = open(sys.argv[1]).read()
code = int(re.search(r"versionCode\s+(\d+)", text).group(1))
name = re.search(r'versionName\s+"([^"]+)"', text).group(1)
print(code, name)
PY
)

NEXT_CODE=$((CURRENT_CODE + 1))
NEXT_NAME="$(python3 - "$CURRENT_NAME" <<'PY'
import sys
parts = sys.argv[1].split(".")
parts[-1] = str(int(parts[-1]) + 1)
print(".".join(parts))
PY
)"

python3 - "$GRADLE_FILE" "$NEXT_CODE" "$NEXT_NAME" <<'PY'
import re, sys
path, code, name = sys.argv[1], sys.argv[2], sys.argv[3]
text = open(path).read()
text = re.sub(r"(versionCode\s+)\d+", rf"\g<1>{code}", text, count=1)
text = re.sub(r'(versionName\s+")[^"]+(")', rf"\g<1>{name}\2", text, count=1)
open(path, "w").write(text)
PY

echo "==> versionCode $CURRENT_CODE -> $NEXT_CODE, versionName $CURRENT_NAME -> $NEXT_NAME"

cd "$ROOT"
./gradlew :presentation:assembleProdDebug --no-daemon

NOTES="${NEXT_NAME} (${NEXT_CODE})"
if [[ -n "$EXTRA_NOTES" ]]; then
  NOTES="${NOTES}: ${EXTRA_NOTES}"
fi

npx --yes firebase-tools appdistribution:distribute \
  "$APK" \
  --app "$FIREBASE_APP_ID" \
  --groups "$TESTER_GROUP" \
  --release-notes "$NOTES" \
  --project "$FIREBASE_PROJECT"

echo "==> Distribuido ${NEXT_NAME} (${NEXT_CODE}) al grupo ${TESTER_GROUP}"
