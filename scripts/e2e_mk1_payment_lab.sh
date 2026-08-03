#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LAB_SUBSCRIPTION_ID="${LAB_SUBSCRIPTION_ID:-900001}"
LAB_PAYMENT_ID="${LAB_PAYMENT_ID:-900001}"
USERNAME="${LAB_ANDROID_USERNAME:-labmk1}"
PASSWORD="${LAB_ANDROID_PASSWORD:-nohacker}"
BACKEND_ROOT="${BACKEND_ROOT:-$(cd "$SCRIPT_DIR/../../ispadmin-backend" 2>/dev/null && pwd || echo "")}"

export USERNAME PASSWORD
export OUT="${OUT:-/tmp/e2e_mk1_payment_lab}"
mkdir -p "$OUT"

# shellcheck disable=SC1091
source "$SCRIPT_DIR/e2e_login_by_tag.sh"

if [[ -n "$BACKEND_ROOT" && -x "$BACKEND_ROOT/scripts/mk1-lab-reset.sh" ]]; then
  export MYSQL_PASSWORD="${MYSQL_PASSWORD:-${SPRING_DATASOURCE_PASSWORD:-}}"
  export ADMIN_USER="${ADMIN_USER:-dscorp}"
  export ADMIN_PASS="${ADMIN_PASS:-nohacker}"
  export ISP_BASE="${ISP_BASE:-http://127.0.0.1:8080/ispadmin}"
  export LAB_IP="${LAB_IP:-192.168.250.1}"
  echo "==> Reset lab fixture (BD + MK1 deudores)"
  "$BACKEND_ROOT/scripts/mk1-lab-reset.sh"
fi

tap_text() {
  local text="$1"
  local json
  json="$(layout_json "text_${text// /_}")"
  local coords
  coords="$(python3 - "$json" "$text" <<'PY'
import json, sys
data = json.load(open(sys.argv[1]))
text = sys.argv[2]
for n in data:
    t = (n.get("text") or "").strip()
    if t == text or text in t:
        x, y = [int(v) for v in n["center"].strip("[]").split(",")]
        print(x, y)
        break
else:
    raise SystemExit(f"text not found: {text}")
PY
)"
  echo "tap text=$text coords=$coords"
  $ADB -s "$DEVICE" shell input tap $coords
}

type_digits() {
  local value="$1"
  local ch key
  for ch in $(echo "$value" | grep -o .); do
    case "$ch" in
      0) key=7 ;;
      1) key=8 ;;
      2) key=9 ;;
      3) key=10 ;;
      4) key=11 ;;
      5) key=12 ;;
      6) key=13 ;;
      7) key=14 ;;
      8) key=15 ;;
      9) key=16 ;;
      *) echo "unsupported digit: $ch" >&2; return 1 ;;
    esac
    $ADB -s "$DEVICE" shell input keyevent "$key"
    sleep 0.2
  done
}

$ADB -s "$DEVICE" reverse tcp:8080 tcp:8080
$ADB -s "$DEVICE" shell am force-stop "$PACKAGE"
$ADB -s "$DEVICE" shell pm clear "$PACKAGE"
$ADB -s "$DEVICE" logcat -c
$ADB -s "$DEVICE" shell am start -n "$PACKAGE/com.dscorp.ispadmin.presentation.ui.features.main.MainActivity"
sleep 4
dismiss_notifications

tap_tag_retry "login_username"
sleep 0.3
$ADB -s "$DEVICE" shell input text "$USERNAME"
sleep 1
tap_tag_retry "login_password"
sleep 0.3
$ADB -s "$DEVICE" shell input text "$PASSWORD"
sleep 1.5
tap_tag_retry "login_submit"
sleep 10
dismiss_notifications

tap_tag_retry "main_nav_open_drawer"
sleep 1
tap_tag_retry "drawer_nav_subscription_finder"
sleep 2

tap_tag_retry "subscription_search_filter_code"
sleep 0.5
tap_tag_retry "subscription_search_query_code"
sleep 0.3
type_digits "$LAB_SUBSCRIPTION_ID"
sleep 6

tap_tag_retry "subscription_result_menu_${LAB_SUBSCRIPTION_ID}"
sleep 1
tap_text "Mostrar historial de pagos"
sleep 3

tap_tag_retry "payment_history_item_${LAB_PAYMENT_ID}"
sleep 2

tap_text "Seleccionar método"
sleep 1
tap_text "Efectivo"
sleep 1
tap_tag_retry "payment_register_submit"
sleep 10

echo "==> Verificar MK1: IP lab fuera de deudores"
python3 <<PY
import json, os, urllib.request
base = os.environ.get("ISP_BASE", "http://127.0.0.1:8080/ispadmin")
user = os.environ.get("ADMIN_USER", "dscorp")
pwd = os.environ.get("ADMIN_PASS", "nohacker")
lab_ip = os.environ.get("LAB_IP", "192.168.250.1")
body = json.dumps({"username": user, "password": pwd}).encode()
token = json.loads(urllib.request.urlopen(urllib.request.Request(f"{base}/users/login", data=body, method="POST", headers={"Content-Type": "application/json"})).read())["accessToken"]
rows = json.loads(urllib.request.urlopen(urllib.request.Request(f"{base}/api/filter-rules/debt-cut/1", headers={"Authorization": "Bearer " + token})).read())
ips = [r.get("srcAddress") for r in rows]
if lab_ip in ips:
    raise SystemExit(f"FAIL: {lab_ip} still in deudores")
print(f"PASS: {lab_ip} not in deudores")
PY

tap_tag_retry "payment_register_success_dismiss" 2>/dev/null || true
echo "E2E_MK1_PAYMENT_LAB_OK sub=$LAB_SUBSCRIPTION_ID pay=$LAB_PAYMENT_ID"
