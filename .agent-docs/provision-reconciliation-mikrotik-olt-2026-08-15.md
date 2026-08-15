# Reconciliación MikroTik + OLT tras registro

Fecha: 2026-08-15  
Rama: `cursor/provision-reconciliation-ef49`

## Comportamiento (backend)

1. El registro **siempre persiste** suscripción e IP si validación e IP están OK.
2. Provisión de red (MikroTik + OLT) es best-effort:
   - `mikrotikProvisionStatus`: `PENDING` | `COMPLETE` | `FAILED`
   - `oltProvisionStatus`: `PENDING` | `COMPLETE` | `FAILED` | `NA` (`NA` en WIRELESS / no-fiber)
3. Job cada **5 minutos** (`subscription.provision.reconciliation.interval-ms=300000`, `initial-delay-ms=60000`).
4. Backoff: intento 0→5 min, 1→15 min, ≥2→30 min; tras **12** intentos → `FAILED` estable.
5. Re-sync con el mismo `clientRequestId` reconcilia solo lo pendiente (idempotente).
6. DTO: `mikrotikProvisionStatus`, `oltProvisionStatus`, `provisioningPending` (true si alguno es `PENDING`/`FAILED`).

Parche backend (si el remoto no acepta push):  
`.agent-docs/backend-provision-reconciliation.patch`

## Android

- Online: diálogo de éxito con mensaje  
  “Registrado; provisión de red pendiente de reconciliar” si `provisioningPending`.
- Sync offline: HTTP 200 (alta o `alreadyRegistered`) → `SubscriptionSyncOutcome.Success`  
  aunque `provisioningPending` → se borra la cola local; el backend reconcilia.
- IP manual offline sin cambios.
