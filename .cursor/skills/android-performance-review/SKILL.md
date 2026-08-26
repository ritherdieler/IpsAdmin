---
name: android-performance-review
description: Investigates IpsAdmin ANR, jank, recomposition, memory, or battery using evidence (traces, ANR logs). Use when the user reports slowness or asks for a performance review. Do not optimize without evidence or during a routine feature.
---

# android-performance-review (IpsAdmin)

No micro-optimizar. Primero medir.

## Workflow

1. Síntoma + evidencia (trace, log ANR, recomposition count, tamaño de lista, query Room).
2. Hipótesis acotada al módulo (Compose vs Room vs Retrofit vs imágenes).
3. Fix mínimo con test o repro steps.
4. Verificar que el síntoma baja. Si no hay métrica, no afirmar ganancia.

## Áreas habituales aquí

- Main thread: Gson/Room/IO en VM sin dispatcher correcto.
- Compose: estado inestable, listas sin key, trabajo en composition.
- Coil: imágenes de comprobantes/fotos de login.
- `Repository.kt` god: N llamadas secuenciales en un intent.
- Startup: `KoinApplication` (Stetho, Crashlytics, muchos módulos). No reordenar en una feature.
- Observability WorkManager: no duplicar workers.

## Prohibido

Reescribir navegación, migrar a DataStore, o “optimizar” con `GlobalScope` / caches sin invalidación.
