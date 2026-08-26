---
name: compose-feature
description: Compose UI craft for IpsAdmin (UDF Screen/Content, MyTheme, testTag, existing NavHost destination). Use when building or changing a Compose screen/list/Material 3 layout. Pair with android-feature for multi-layer work. Do not use for XML Fragments or for wiring a brand-new route (create-compose-screen).
---

# compose-feature (IpsAdmin)

No usar para Fragments XML. Tema: `MyTheme`. Componentes: `composecomponents`.

## Estructura

```text
FooScreen.kt          // koinViewModel, collectAsStateWithLifecycle, onIntent
FooContent.kt         // state + lambdas, preview
FooViewModel.kt       // UiState, Intent, UiEvent
FooUi.kt              // sealed/data classes
FooViewModelTest.kt
FooScreenTest.kt      // Robolectric + testTag (flujos críticos)
```

Pantalla trivial puede vivir Screen+Content en un archivo, como el vecino.

## Pasos

1. Copiar `PendingSubscriptionsScreen` / `PendingSubscriptionsViewModel`.
2. Ruta `@Serializable` en `NavRoutes.kt` y graph (`FeatureNavGraph` / `AuthNavGraph`).
3. `koinViewModel()` solo en Screen. Content stateless.
4. Interactivos: `Modifier.testTag`. Íconos: `contentDescription`.
5. `LaunchedEffect` para `Load` y para `uiEvent` (snackbar/nav).
6. Listas: `LazyColumn(items(items, key = { it.id })`.
7. Tests: skill `test-viewmodel` + tags en UI test si el flujo es crítico.

## Prohibido

Networking en Composable. `init` en ViewModel. `collectAsState()` en pantallas. Navegar con `findNavController` en Compose nuevo.
