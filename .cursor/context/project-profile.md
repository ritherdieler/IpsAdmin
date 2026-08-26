# IpsAdmin (IspAdmin Android) — Project Profile

Fuente de verdad auditada. No inventar stack ni arquitectura.

## Identidad

| Campo | Valor |
|---|---|
| App | IspAdmin (`app_name`) |
| Gradle root | `ispAdminAndroid` |
| applicationId / namespace | `com.dscorp.ispadmin` |
| Módulos | `:presentation` (app), `:domain` (JVM), `:data` (Android lib), `:observability` (Android lib) |
| minSdk / targetSdk / compileSdk | 26 / 34 / 36 |
| Kotlin plugin / stdlib | 2.1.0 / 2.1.21 (hay desfase; no “arreglarlo” en una feature) |
| AGP / Gradle / JVM | 8.13.2 / 8.13 / Java 17 |
| DSL | Groovy (`.gradle`, no `.kts`, no version catalog TOML) |
| Flavors | `dev`, `prod` (dimension `environment`) |
| versionCode / versionName | ver `presentation/build.gradle` |

`dev` → `applicationIdSuffix ".dev"`, `BASE_URL = http://127.0.0.1:8080/ispadmin/`  
`prod` → `BASE_URL = https://api.gigafiberperu.cloud/ispadmin/`

## Arquitectura REAL

Híbrido en migración, no Clean Architecture completa.

```
:presentation  →  :domain, :data, :observability
:data          →  :domain
:domain        →  Kotlin + coroutines + Koin core (sin Android)
```

- Dominio **nuevo** (offline sync, catálogo): puertos en `:domain`, impl en `:data`.
- Dominio **legado**: `presentation/src/main/java/com/dscorp/ispadmin/domain/` (~75 archivos).
- Data **legado**: `IRepository` + `Repository.kt` (~1124 líneas) en `:presentation`.
- UI **nueva**: Jetpack Compose + Navigation Compose + Material 3 + `MyTheme`.
- UI **legado**: ~33 XML layouts, ~11 Fragments, ViewBinding/DataBinding, algo de LiveData.

No migrar XML→Compose ni romper `IRepository` salvo tarea explícita.

## Patrones de estado

**Canónico (código nuevo):** UDF / MVI ligero.

- `UiState` (`data class`) + `StateFlow`
- `Intent` / `onIntent`
- `UiEvent` via `SharedFlow` (one-shot)
- Dispatcher inyectable (`mainImmediate`) para tests
- Ejemplo: `presentation/.../subscription/pending/PendingSubscriptionsViewModel.kt`

**Legado (no copiar en código nuevo):** `IRepository` directo, `init { load() }`, LiveData, try/catch en ViewModel.

## Stack real

| Área | Tecnología |
|---|---|
| DI | Koin (no Hilt) — `di/KoinApplication.kt` |
| Red | Retrofit + OkHttp + Gson. Kotlin Serialization solo para rutas `@Serializable` |
| Local | Room en `:data` (sync/catálogo). SharedPreferences + `TokenStore` para sesión |
| Imágenes | Coil |
| Nav | Navigation Compose (`IpsAdminNavHost.kt`, `NavRoutes.kt`). Sin `navigation/*.xml` |
| Firebase | Analytics, Messaging, Crashlytics, App Distribution |
| Tests | JUnit 4, MockK, Robolectric + Compose UI Test en `src/test`. **0 androidTest** |
| Turbine / Detekt / Ktlint / Spotless | No |

## Comandos reales (Windows)

```bat
gradlew.bat :presentation:compileDevDebugKotlin
gradlew.bat :presentation:testDevDebugUnitTest --tests "com.dscorp.ispadmin.<Fqcn>"
gradlew.bat :domain:test
gradlew.bat :data:test
gradlew.bat :presentation:lintDevDebug
gradlew.bat :presentation:assembleProdDebug
gradlew.bat :presentation:installDevDebug
gradlew.bat adbReverseAll
```

No existe `installDevDebugWithReverse`. Usar `debugWithReverse` o `adbReverseAll`.  
No existe Spotless/Detekt. `InstrumentationTestRunner` está referenciado en Gradle y **no está en el repo**.

## Archivos canónicos

- ViewModel UDF: `presentation/.../subscription/pending/PendingSubscriptionsViewModel.kt`
- Screen Compose: `presentation/.../subscription/pending/PendingSubscriptionsScreen.kt`
- UseCase real: `domain/.../usecase/subscription/SyncPendingSubscriptionsUseCase.kt`
- Puerto + impl: `domain/.../PendingSubscriptionRepository.kt` + `data/.../PendingSubscriptionRepositoryImpl.kt`
- API monolítica: `presentation/.../datasource/remote/RestApiServices.kt`
- API acotada: `presentation/.../auth/AuthApiService.kt`
- Mapper: `data/.../local/mapper/CatalogMapper.kt`
- Test VM: `presentation/src/test/.../pending/PendingSubscriptionsViewModelTest.kt`
- Nav: `presentation/.../navigation/IpsAdminNavHost.kt`
- App: `presentation/.../di/KoinApplication.kt`

## Vocabulario de dominio

Alinear con el backend WISP: **Subscription** (no Client/Customer), **Payment** (no Invoice), **Plan**, **Place**, **NapBox**, **User** (staff).
