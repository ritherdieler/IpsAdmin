# Deploy develop — offline sync + provisión (2026-08-15)

## Android

- Rama `develop` en GitHub ya incluye los cambios (`378d4d85`).
- Distribuir: `./scripts/firebase-distribute-prod-debug.sh "offline sync + provisión pendiente"`

## Backend

Push a `ispadmin-backend` **no disponible** desde el agente (`cursor[bot]` sin permiso).

Estado local listo en el clone del agente / aplicar en tu máquina:

1. En `ispadmin-backend`, checkout `develop` y aplicar:
   - Parche completo: `IpsAdmin-android/.agent-docs/backend-develop-offline-provision.patch`
   ```bash
   git checkout develop && git pull
   git am --3way /ruta/a/backend-develop-offline-provision.patch
   # o: git apply si am falla
   ```
2. Migraciones Flyway en develop:
   - `V21` = `subscription_is_bimonthly` (ya existía)
   - `V22` = `subscription_client_request_id`
   - `V23` = `subscription_provision_status`
3. Deploy:
   ```bash
   ./scripts/deploy.sh
   ```

## Contenido del merge

- Offline `clientRequestId` + registro idempotente
- `IP_CONFLICT` + alerta WhatsApp NOC
- Soft-fail MikroTik/OLT + job reconciliación 5 min + `provisioningPending` en DTO
