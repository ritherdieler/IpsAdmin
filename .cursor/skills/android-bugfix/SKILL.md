---
name: android-bugfix
description: Diagnoses and minimally fixes a defect in IpsAdmin (crash, back-navigation, wrong UI state, sync, regression). Use when something is broken. Do not use for new features or refactors that must preserve behavior.
---

# android-bugfix (IpsAdmin)

## Workflow

```
- [ ] Reproducir / evidencia
- [ ] Root cause (archivo + por qué)
- [ ] Regression test (RED)
- [ ] Fix mínimo (GREEN)
- [ ] Verificar
- [ ] Efectos secundarios
```

## 1. Reproducir

Logs, Crashlytics, pasos UI, flavor (`dev`/`prod`). Distinguir error de red vs bug local.

## 2. Root cause

Leer el flujo real (Compose NavHost vs Fragment). No parchear la Screen si el fallo está en `Repository`/`UseCase`.

Preguntar: ¿lifecycle? ¿race en Flow? ¿`body()!!`? ¿flavor BASE_URL? ¿Room vs red?

## 3. Regression test

Primero un test que falle por el bug. ViewModel/UseCase preferible a UI.

## 4. Fix mínimo

No migrar arquitectura ni “limpiar” `Repository.kt` de paso. No tragarse excepciones.

## 5. Verify

```bat
gradlew.bat :presentation:testDevDebugUnitTest --tests "Fqcn"
gradlew.bat :presentation:compileDevDebugKotlin
```

Revisar otras pantallas que compartan el estado/repo tocado.
