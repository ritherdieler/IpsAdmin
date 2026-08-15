# Reconciliación MikroTik + OLT tras registro

Fecha: 2026-08-15  
Rama: `develop`

## Comportamiento (backend)

1. El registro **siempre persiste** suscripción e IP si validación e IP están OK.
2. Provisión de red (MikroTik + OLT) es best-effort:
   - `mikrotikProvisionStatus`: `PENDING` | `COMPLETE` | `FAILED`
   - `oltProvisionStatus`: `PENDING` | `COMPLETE` | `FAILED` | `NA` (`NA` en WIRELESS / no-fiber)
3. Job cada **5 minutos** (`subscription.provision.reconciliation.interval-ms=300000`, `initial-delay-ms=60000`).
4. Backoff: intento 0→5 min, 1→15 min, ≥2→30 min; tras **12** intentos → `FAILED` estable.
5. Re-sync con el mismo `clientRequestId` reconcilia solo lo pendiente (idempotente).
6. DTO: `mikrotikProvisionStatus`, `oltProvisionStatus`, `provisioningPending` (true si alguno es `PENDING`/`FAILED`).

Migraciones en develop: `V22` client_request_id, `V23` provision_status.

Parche backend completo para apply en `ispadmin-backend` develop:  
`.agent-docs/backend-develop-offline-provision.patch`

## Android

- Online: diálogo de éxito parcial si `provisioningPending`.
- Sync offline HTTP 200 (aunque `provisioningPending`) borra cola local.
