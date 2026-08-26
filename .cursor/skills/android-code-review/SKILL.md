---
name: android-code-review
description: Reviews an IpsAdmin diff/PR with BLOCKER/HIGH/MEDIUM/LOW findings. Use when the user says review, PR, or code review. Do not implement changes unless asked.
---

# android-code-review (IpsAdmin)

Prioridad: correctness → testability → readability → maintainability → architecture → performance → cleverness.

## Severidad

- **BLOCKER** — crash, pérdida de datos, secreto filtrado, auth rota, ANR probable, test que no prueba el bug
- **HIGH** — lógica incorrecta, leak, race, DTO en UI nueva, `IRepository` crecido sin necesidad
- **MEDIUM** — SOLID/claridad, lifecycle, test gaps
- **LOW** — nit no bloqueante. Evitar comentarios estéticos.

## Checklist (solo lo aplicable al diff)

- Comportamiento y edges (null, vacío, 401, offline)
- ¿Sigue el patrón del módulo (UDF vs legado)?
- Kotlin: `!!`, `GlobalScope`, `catch (e: Exception)` vacío
- Compose: UDF, `testTag`, sin red en Composable, keys en Lazy
- XML: binding, `viewLifecycleOwner`
- Coroutines: `viewModelScope`, cancelación, dispatcher inyectable
- Koin registrado
- Tokens fuera de logs
- minSdk 26
- Tests MockK/JUnit4 que fallen si se revierte el cambio
- Gradle: deps nuevas injustificadas

No exigir UseCase en un VM legado que ya usa `IRepository`.
