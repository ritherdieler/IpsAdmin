---
name: create-compose-screen
description: Scaffolds a brand-new IpsAdmin Navigation Compose destination (NavRoutes + graph + Koin + Screen stubs). Use only when adding a route/screen that does not exist yet. Do not use when editing an existing Screen (compose-feature).
---

# create-compose-screen (IpsAdmin)

1. Copiar estructura de `PendingSubscriptionsScreen` (no un Fragment).
2. Añadir ruta `@Serializable` en `NavRoutes.kt`.
3. Registrar composable en `FeatureNavGraph` o `AuthNavGraph`.
4. `viewModel { FooViewModel(...) }` en `viewModelModule`.
5. Screen: `koinViewModel()`, `collectAsStateWithLifecycle`, `LaunchedEffect(Unit) { onIntent(Load) }`.
6. Content: `testTag` en botones/inputs.
7. Tests: `test-viewmodel` + Screen test si hay interacción crítica.
8. `gradlew.bat :presentation:compileDevDebugKotlin` y test del VM.

No crear UseCase si el VM solo llama un repo ya acotado **y** el módulo vecino inyecta el repo. En features nuevas de `:domain`, preferir UseCase como pending.
