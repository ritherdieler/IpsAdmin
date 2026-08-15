# Deploy develop — offline sync + provisión (2026-08-15)

## Android

- Publicado en App Distribution: **2.6.4 (56)** (`prodDebug`, grupo `gigafiber`).
- Rama `develop` local incluye el bump de versión (sin push).

## Backend

- Desplegado en prod: **`1.0.3+bce3bc2`** (`./scripts/deploy.sh --deploy` OK).
- `develop` local está **ahead** de `origin/develop` (sin push).

Migraciones Flyway en prod:

- `V21` = `subscription_is_bimonthly` (ya existía; checksum intacto)
- `V22` = `subscription_client_request_id`
- `V23` = `subscription_provision_status`

## Contenido del merge

- Offline `clientRequestId` + registro idempotente
- `IP_CONFLICT` + alerta WhatsApp NOC
- Soft-fail MikroTik/OLT + job reconciliación 5 min + `provisioningPending` en DTO
