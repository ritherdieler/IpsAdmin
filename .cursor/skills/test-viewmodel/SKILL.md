---
name: test-viewmodel
description: Recipe for IpsAdmin *ViewModelTest (StandardTestDispatcher, setMain, MockK, uiState). Use when writing or fixing a ViewModel unit test file. Do not use for UseCase-only tests or to decide whether a test is needed.
---

# test-viewmodel (IpsAdmin)

Canónico: `PendingSubscriptionsViewModelTest.kt`.

```kotlin
@get:Rule val mainDispatcherRule = // o setMain en @Before/@After
val dispatcher = StandardTestDispatcher()
val vm = FooViewModel(useCase, mainImmediate = dispatcher)

@Test
fun `should show items when load succeeds`() = runTest(dispatcher) {
    coEvery { useCase() } returns Result.success(sample)
    vm.onIntent(FooIntent.Load)
    advanceUntilIdle()
    assertEquals(expected, vm.uiState.value.items)
}
```

- `Dispatchers.setMain(dispatcher)` en `@Before`, `resetMain()` en `@After`.
- No Turbine salvo que el test de SharedFlow sea ilegible sin él (justificar).
- Mock UseCases, no `IRepository`, si el VM canónico usa UseCases.
- Cubrir: success, error, empty, no doble sync (`syncJob?.isActive`).
- Ejecutar: `gradlew.bat :presentation:testDevDebugUnitTest --tests "...FooViewModelTest"`
