---
name: android-security-review
description: Security review of IpsAdmin (TokenStore, Manifest exported, cleartext, secrets, FCM, logs). Use when auditing auth/storage/Manifest or the user asks for a security review. Do not use for generic PR review (android-code-review).
---

# android-security-review (IpsAdmin)

## Workflow

1. Diff: Manifest, `network_security_config`, `local.properties` usages, `TokenStore`, interceptors, WebView (si aparece).
2. Clasificar BLOCKER / HIGH / MEDIUM / LOW.
3. No “arreglar” todo el legado de storage en una review de feature. Sí bloquear secretos nuevos mal puestos.

## Checks

- Secretos: no keys en git. `OBS_API_KEY_ANDROID` / Maps solo en `local.properties`.
- Logs/Crashlytics: sin tokens, passwords, `Authorization`.
- HTTPS prod. No ampliar cleartext a hosts públicos.
- `exported` en Activities/Services/Receivers. Validar extras de Intents.
- Deep links: validar path/params.
- FCM: `CloudMessagingService` — confirmar registro en Manifest.
- Prefs: no añadir más passwords en claro. `allowBackup=true` es riesgo conocido.
- R8: no keep rules que expongan debug.
- Permisos: mínimo. No `QUERY_ALL_PACKAGES` etc. sin necesidad.

Referencia Android: `Skills/security/android-intent-security/SKILL.md` solo si el workspace backend está abierto; en este repo aplica el mismo criterio de Intents.
