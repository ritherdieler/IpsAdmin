# Colisión de IP en sync offline (asignación manual)

Fecha: 2026-08-15  
Ramas: `cursor/ip-conflict-offline-sync-ef49` (android + backend)

## Decisión

- La IP la coordinan los técnicos a mano; la app no asigna ni sugiere IPs.
- Offline: el técnico ingresa `clientIpAddress`.
- Si al sincronizar hay colisión de IP, el backend responde `IP_CONFLICT` y alerta al NOC por WhatsApp.

## Backend

- Clasificador: `SubscriptionIntegrityViolationClassifier` distingue unique de `subscription.ip` vs otros (dni, client_request_id).
- En `POST /subscription` y `POST /subscription/with-facade-photo`, ante `DataIntegrityViolationException` de IP:
  - `status: 409`
  - `errorCode: "IP_CONFLICT"`
  - mensaje accionable
  - alerta WhatsApp al NOC (`net.diag.whatsapp.noc-phone`, plantilla `noc_alert_v1`) vía `SubscriptionIpConflictNocNotifier` (best-effort; no depende de `net.diag.enabled`).
- Otros uniques siguen con 409 genérico sin `errorCode` ni alerta NOC.

## Android

- `SubscriptionSyncOutcome.IpConflict` cuando el body (o error body) trae `status=409` y `errorCode=IP_CONFLICT`.
- El sync de pendientes parsea el JSON de `BaseResponse` (el backend suele devolver HTTP 200 con `status` en el body).
- `SyncPendingSubscriptionsUseCase` marca `CONFLICT`, conserva fila/foto y expone:  
  `IP ya en uso. Coordina otra IP con el equipo e intenta de nuevo.`
- La pantalla de pendientes muestra ese mensaje en `errorMessage`.

## Fuera de alcance

- Asignación automática / catálogo de pools en el teléfono.
- Edición de IP de un pendiente desde la UI (se conserva el registro para seguimiento operativo).
