#!/usr/bin/env bash
set -euo pipefail

ADB="${ADB:-$HOME/Library/Android/sdk/platform-tools/adb}"
DEVICE="${DEVICE:-$($ADB devices | awk '/device$/{print $1; exit}')}"
PACKAGE="${PACKAGE:-com.dscorp.ispadmin.dev}"
OUT="${OUT:-/tmp/e2e_login_tag}"
USERNAME="${USERNAME:-dscorp}"
PASSWORD="${PASSWORD:-nohacker}"

mkdir -p "$OUT"

layout_json() {
  local name="$1"
  android layout --device="$DEVICE" -o "$OUT/$name.json" >/dev/null
  echo "$OUT/$name.json"
}

find_center_by_tag() {
  local json="$1"
  local tag="$2"
  python3 - "$json" "$tag" <<'PY'
import json, sys
path, tag = sys.argv[1], sys.argv[2]
data = json.load(open(path))

def rid_of(n):
    return n.get("resource-id") or n.get("resourceId") or ""

candidates = [n for n in data if rid_of(n) == tag or rid_of(n).endswith("/" + tag)]
if not candidates:
    candidates = [n for n in data if tag in rid_of(n)]
if not candidates:
    raise SystemExit(f"tag not found: {tag}")

def score(n):
    ints = set(n.get("interactions") or [])
    s = 0
    if "clickable" in ints:
        s -= 10
    if "focusable" in ints:
        s -= 1
    if n.get("text"):
        s -= 1
    return s

candidates.sort(key=score)
node = candidates[0]
center = node.get("center")
if not center:
    raise SystemExit(f"no center for tag: {tag} node={node}")
x, y = [int(v) for v in center.strip("[]").split(",")]
print(x, y)
print(f"matched rid={rid_of(node)} interactions={node.get('interactions')} text={node.get('text')}", file=sys.stderr)
PY
}

tap_tag() {
  local tag="$1"
  local json
  json="$(layout_json "layout_$tag")"
  local coords
  if ! coords="$(find_center_by_tag "$json" "$tag")"; then
    return 1
  fi
  echo "tap tag=$tag coords=$coords"
  $ADB -s "$DEVICE" shell input tap $coords
}

hide_keyboard() {
  $ADB -s "$DEVICE" shell input keyevent KEYCODE_BACK
  sleep 0.6
}

dismiss_notifications() {
  local attempt
  for attempt in 1 2 3; do
    local json
    json="$(layout_json "deny_$attempt")"
    python3 - "$json" <<'PY' > "$OUT/deny.txt" || true
import json, sys
data = json.load(open(sys.argv[1]))
for n in data:
    rid = n.get("resource-id") or n.get("resourceId") or ""
    text = n.get("text") or ""
    if text == "No permitir" or rid.endswith("permission_deny_and_dont_ask_again_button") or rid.endswith("permission_deny_button"):
        print(*[int(x) for x in n["center"].strip("[]").split(",")])
        break
PY
    if [[ -s "$OUT/deny.txt" ]]; then
      $ADB -s "$DEVICE" shell input tap $(cat "$OUT/deny.txt")
      sleep 1.2
      : > "$OUT/deny.txt"
    else
      break
    fi
  done
}

tap_tag_retry() {
  local tag="$1"
  local tries="${2:-4}"
  local i
  for i in $(seq 1 "$tries"); do
    dismiss_notifications
    if tap_tag "$tag"; then
      return 0
    fi
    sleep 1
  done
  echo "failed to find tag=$tag" >&2
  layout_json "missing_$tag" >/dev/null || true
  python3 - <<PY
import json
data=json.load(open("$OUT/missing_$tag.json"))
print("available tags", sorted({(n.get("resource-id") or n.get("resourceId")) for n in data if (n.get("resource-id") or n.get("resourceId"))}))
print("texts", [n.get("text") for n in data if n.get("text")][:15])
PY
  return 1
}

field_text() {
  local tag="$1"
  local json="$2"
  python3 - "$json" "$tag" <<'PY'
import json, sys
path, tag = sys.argv[1], sys.argv[2]
data = json.load(open(path))
for n in data:
    rid = n.get("resource-id") or n.get("resourceId") or ""
    if rid == tag or rid.endswith("/" + tag):
        print(n.get("text") or "")
        break
PY
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
$ADB -s "$DEVICE" reverse tcp:8080 tcp:8080
$ADB -s "$DEVICE" shell am force-stop "$PACKAGE"
$ADB -s "$DEVICE" shell pm clear "$PACKAGE"
$ADB -s "$DEVICE" logcat -c
$ADB -s "$DEVICE" shell am start -n "$PACKAGE/com.dscorp.ispadmin.presentation.ui.features.main.MainActivity"
sleep 4
dismiss_notifications

tap_tag_retry "login_username"
sleep 0.4
$ADB -s "$DEVICE" shell input text "$USERNAME"
sleep 2.5

tap_tag_retry "login_password"
sleep 0.4
$ADB -s "$DEVICE" shell input text "$PASSWORD"
sleep 1.0

hide_keyboard
sleep 2.0
dismiss_notifications

PRE="$(layout_json pre)"
echo "username=$(field_text login_username "$PRE")"
echo "password=$(field_text login_password "$PRE")"

tap_tag_retry "login_submit"
sleep 12

layout_json done >/dev/null
python3 - <<'PY'
import json
data = json.load(open("/tmp/e2e_login_tag/done.json"))
print("DONE", [n.get("text") for n in data if n.get("text")][:12])
PY

PID="$($ADB -s "$DEVICE" shell pidof "$PACKAGE" | tr -d '\r')"
$ADB -s "$DEVICE" logcat -d --pid="$PID" | rg "users/login| <-- 20" | tail -20 | tee "$OUT/log.txt"
fi
