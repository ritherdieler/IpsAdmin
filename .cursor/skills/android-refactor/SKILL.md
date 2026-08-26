---
name: android-refactor
description: Behavior-preserving restructure in IpsAdmin (extract UseCase, split IRepository, move code). Use when the user says refactor without changing product behavior. Do not use to add features or to fix crashes.
---

# android-refactor (IpsAdmin)

No mezclar refactor masivo con una feature. No migrar XML→Compose ni Hilt salvo pedido.

## Workflow

1. Entender comportamiento observable (UI + APIs + persistencia).
2. Red de tests: existentes + characterization si falta.
3. Cambios **pequeños** y reversibles. Un tema por PR/turno.
4. Verificar tras cada paso.

```bat
gradlew.bat :presentation:testDevDebugUnitTest
gradlew.bat :domain:test
gradlew.bat :data:test
```

5. Diff enfocado: no reformatear el módulo, no bump de deps, no renombrar paquetes ajenos.

## Extraer de `IRepository`

Permitido si la tarea lo pide: puerto específico en `:domain`, impl/adapter, VM deja de depender del god repo. Comportamiento HTTP/Room idéntico. Tests del adapter.

## Señales de stop

Si no puedes demostrar comportamiento con tests, no extraigas capas. Documenta el riesgo y para.
