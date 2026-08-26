---
name: android-tdd
description: Runs RED→GREEN→REFACTOR in IpsAdmin with a failing Gradle test before production code. Use when adding ViewModel/UseCase/mapper validation or the user says TDD/test first. Do not use to choose the test pyramid (android-testing) or only to paste ViewModelTest boilerplate (test-viewmodel).
---

# android-tdd (IpsAdmin)

Mostrar con **comandos reales** que el test falló antes de implementar. No afirmar TDD en retrospectiva.

## RED

1. Escribir el test de comportamiento (AAA / Given-When-Then).
2. Nombre: `` `should emit error when sync fails` ``.
3. Ejecutar y confirmar fallo por comportamiento ausente:

```bat
gradlew.bat :presentation:testDevDebugUnitTest --tests "com.dscorp.ispadmin....FooTest"
```

Si el test no compila, arréglalo antes de producción — eso no es RED válido.

## GREEN

Mínimo código de producción. Sin features extra.

## REFACTOR

Claridad, extracción, nombres. Tests siguen verdes.

## Recetas de este repo

**ViewModel:** constructor con UseCases + `mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate`. En test: `StandardTestDispatcher`, `Dispatchers.setMain`, `advanceUntilIdle`. MockK `coEvery`. Canónico: `PendingSubscriptionsViewModelTest`.

**UseCase:** mock del puerto de dominio, no de Retrofit.

**Compose UI:** Robolectric + `createComposeRule` + `testTag` en `src/test`. No hay `androidTest`.

**Doubles:** MockK para puertos. Fake si el colaborador tiene comportamiento (in-memory repo). No mockear data classes.

## Legacy

Sin tests: characterization del comportamiento actual → cambio → test del bug. No reescribir la clase para TDD.

Herramientas: JUnit 4 + MockK. No agregar Turbine/Mockito.
