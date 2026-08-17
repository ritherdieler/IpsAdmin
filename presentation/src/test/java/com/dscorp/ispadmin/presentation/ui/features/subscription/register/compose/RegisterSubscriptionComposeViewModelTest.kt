package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import com.dscorp.ispadmin.domain.model.CatalogCoreDevice
import com.dscorp.ispadmin.domain.model.CatalogNapBox
import com.dscorp.ispadmin.domain.model.CatalogOnu
import com.dscorp.ispadmin.domain.model.CatalogPlan
import com.dscorp.ispadmin.domain.model.InstallationOrder
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.NapBoxResponse
import com.dscorp.ispadmin.domain.model.NetworkDevice
import com.dscorp.ispadmin.domain.model.Onu
import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.Place
import com.dscorp.ispadmin.domain.model.PlanResponse
import com.dscorp.ispadmin.domain.model.RegistrationCatalog
import com.dscorp.ispadmin.domain.model.Subscription
import com.dscorp.ispadmin.domain.model.User
import com.dscorp.ispadmin.domain.usecase.InstallationOrderUseCase
import com.dscorp.ispadmin.domain.usecase.catalog.GetRegistrationCatalogUseCase
import com.dscorp.ispadmin.domain.usecase.catalog.RefreshRegistrationCatalogUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.GetAvailableOnuListUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.GetNearNapBoxesUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.GetPlaceFromLocationUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.GetUserSessionUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.ObserveOfflineRegistrationModeUseCase
import com.dscorp.ispadmin.domain.usecase.subscription.RegisterSubscriptionResult
import com.dscorp.ispadmin.domain.usecase.subscription.RegisterSubscriptionUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.models.RegisterSubscriptionIntent
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.models.RegisterSubscriptionUiEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterSubscriptionComposeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAvailableOnuListUseCase: GetAvailableOnuListUseCase
    private lateinit var getRegistrationCatalogUseCase: GetRegistrationCatalogUseCase
    private lateinit var refreshRegistrationCatalogUseCase: RefreshRegistrationCatalogUseCase
    private lateinit var getPlaceFromLocationUseCase: GetPlaceFromLocationUseCase
    private lateinit var registerSubscriptionUseCase: RegisterSubscriptionUseCase
    private lateinit var getUserSessionUseCase: GetUserSessionUseCase
    private lateinit var getNearNapBoxesUseCase: GetNearNapBoxesUseCase
    private lateinit var installationOrderUseCase: InstallationOrderUseCase
    private lateinit var observeOfflineRegistrationModeUseCase: ObserveOfflineRegistrationModeUseCase
    private val offlineModeFlow = MutableStateFlow(false)

    private lateinit var viewModel: RegisterSubscriptionComposeViewModel

    private val sampleUser = User(id = 1, name = "T", lastName = "U")
    private val samplePlan = PlanResponse(
        id = "p1",
        name = "Plan",
        price = 10.0,
        downloadSpeed = "100",
        uploadSpeed = "100",
        type = InstallationType.FIBER
    )
    private val sampleCoreDevice = NetworkDevice(id = 10, name = "Core-A", disabled = false)
    private val secondCoreDevice = NetworkDevice(id = 11, name = "Core-B", disabled = false)

    private lateinit var facadePhotoFile: File

    private fun sampleCatalog(
        plans: List<CatalogPlan> = listOf(
            CatalogPlan(
                id = "p1",
                name = "Plan",
                price = 10.0,
                downloadSpeed = "100",
                uploadSpeed = "100",
                type = "FIBER"
            )
        ),
        napBoxes: List<CatalogNapBox> = emptyList(),
        onus: List<CatalogOnu> = emptyList(),
        coreDevices: List<CatalogCoreDevice> = listOf(
            CatalogCoreDevice(id = 10, name = "Core-A", disabled = false)
        )
    ) = RegistrationCatalog(
        plans = plans,
        places = emptyList(),
        napBoxes = napBoxes,
        onus = onus,
        coreDevices = coreDevices
    )

    private fun fiberNap() = CatalogNapBox(id = "n1", placeName = "P1", placeId = 1)

    private fun fiberOnu() = CatalogOnu(
        sn = "sn1",
        board = "b",
        oltId = "olt",
        onu = "1",
        onuTypeId = "t",
        onuTypeName = "type",
        ponType = "pon",
        port = "p"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        facadePhotoFile = File.createTempFile("facade_test", ".jpg")
        getAvailableOnuListUseCase = mockk()
        getRegistrationCatalogUseCase = mockk()
        refreshRegistrationCatalogUseCase = mockk()
        getPlaceFromLocationUseCase = mockk()
        registerSubscriptionUseCase = mockk()
        getUserSessionUseCase = mockk()
        getNearNapBoxesUseCase = mockk()
        installationOrderUseCase = mockk(relaxed = true)
        observeOfflineRegistrationModeUseCase = mockk()
        every { observeOfflineRegistrationModeUseCase() } returns Result.success(offlineModeFlow)

        coEvery { getAvailableOnuListUseCase() } returns Result.success(emptyList())
        coEvery { refreshRegistrationCatalogUseCase() } returns Result.success(Unit)
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(sampleCatalog())
        coEvery { getUserSessionUseCase() } returns Result.success(sampleUser)

        viewModel = RegisterSubscriptionComposeViewModel(
            getAvailableOnuListUseCase = getAvailableOnuListUseCase,
            getRegistrationCatalogUseCase = getRegistrationCatalogUseCase,
            refreshRegistrationCatalogUseCase = refreshRegistrationCatalogUseCase,
            getPlaceFromLocationUseCase = getPlaceFromLocationUseCase,
            registerSubscriptionUseCase = registerSubscriptionUseCase,
            getUserSessionUseCase = getUserSessionUseCase,
            getNearNapBoxesUseCase = getNearNapBoxesUseCase,
            installationOrderUseCase = installationOrderUseCase,
            observeOfflineRegistrationModeUseCase = observeOfflineRegistrationModeUseCase,
            observabilityClient = mockk(relaxed = true),
            mainImmediate = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadScreenData applies catalog and clears loading`() = runTest(testDispatcher) {
        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        assertTrue(events.isEmpty())
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(sampleUser, viewModel.uiState.value.currentUser)
        assertEquals(samplePlan, viewModel.uiState.value.registerSubscriptionForm.selectedPlan)
        coVerify(exactly = 1) { refreshRegistrationCatalogUseCase() }
        coVerify(exactly = 1) { getRegistrationCatalogUseCase() }

        job.cancel()
    }

    @Test
    fun `loadScreenData emits error when user session fails`() = runTest(testDispatcher) {
        coEvery { getUserSessionUseCase() } returns Result.failure(Exception("no session"))

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertTrue(events[0] is RegisterSubscriptionUiEvent.Error)
        assertEquals("no session", (events[0] as RegisterSubscriptionUiEvent.Error).message)
        assertEquals(false, viewModel.uiState.value.isLoading)

        job.cancel()
    }

    @Test
    fun `loadScreenData merges installation order after catalog`() = runTest(testDispatcher) {
        val place = Place(id = "5", name = "Lima")
        val order = InstallationOrder(
            id = 99,
            customerFirstName = "Ana",
            customerLastName = "Lopez",
            customerAddress = "Av 1",
            customerPhone = "999999999",
            customerDni = "12345678",
            place = place
        )
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(CatalogNapBox(id = "1", placeName = "Lima", placeId = 5)))
        )
        coEvery { installationOrderUseCase.getInstallationOrderByIdResult(99) } returns Result.success(order)

        viewModel.loadScreenData(99)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(99, viewModel.uiState.value.orderId)
        val form = viewModel.uiState.value.registerSubscriptionForm
        assertEquals("Ana", form.firstName)
        assertEquals("Lopez", form.lastName)
        assertEquals(place, form.selectedPlace)
    }

    @Test
    fun `refreshOnuList emits error on failure`() = runTest(testDispatcher) {
        coEvery { getAvailableOnuListUseCase() } returns Result.failure(Exception("onu fail"))

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onIntent(RegisterSubscriptionIntent.RefreshOnuList)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals("onu fail", (events[0] as RegisterSubscriptionUiEvent.Error).message)

        job.cancel()
    }

    @Test
    fun `saveSubscription emits success when register succeeds`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val registered = Subscription(subscriptionId = 1, firstName = "A", lastName = "B")
        coEvery {
            registerSubscriptionUseCase(any(), any(), facadePhotoFile = any())
        } answers {
            assertEquals(true, firstArg<Subscription>().autoCut)
            Result.success(RegisterSubscriptionResult.Registered(registered))
        }

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        assertEquals(1, events.size)
        val success = events[0] as RegisterSubscriptionUiEvent.Success
        assertEquals(registered, success.subscription)
        assertEquals(false, viewModel.uiState.value.isLoading)

        job.cancel()
    }

    @Test
    fun `saveSubscription emits QueuedOffline when register is queued locally`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val pending = PendingSubscription(
            localId = "local-1",
            clientRequestId = "client-1",
            subscriptionJson = "{}",
            createdAt = 1L
        )
        coEvery {
            registerSubscriptionUseCase(any(), any(), facadePhotoFile = any())
        } returns Result.success(RegisterSubscriptionResult.QueuedOffline(pending))

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(RegisterSubscriptionUiEvent.QueuedOffline, events[0])
        assertEquals(false, viewModel.uiState.value.isLoading)

        job.cancel()
    }

    @Test
    fun `saveSubscription emits error when register fails`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        coEvery { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) } returns Result.failure(Exception("backend"))

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals("backend", (events[0] as RegisterSubscriptionUiEvent.Error).message)

        job.cancel()
    }

    @Test
    fun `processCurrentLocation keeps only latest nearby nap result`() = runTest(testDispatcher) {
        val slowNap = NapBoxResponse(id = "a", placeName = "A", placeId = 1)
        val fastNap = NapBoxResponse(id = "b", placeName = "B", placeId = 2)
        coEvery { getPlaceFromLocationUseCase(any(), any()) } returns Result.success(
            Place(id = "1", name = "P")
        )
        coEvery { getNearNapBoxesUseCase(1.0, 1.0) } coAnswers {
            delay(10_000)
            Result.success(listOf(slowNap))
        }
        coEvery { getNearNapBoxesUseCase(2.0, 2.0) } returns Result.success(listOf(fastNap))

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.processCurrentLocation(1.0, 1.0)
        advanceTimeBy(5)
        viewModel.processCurrentLocation(2.0, 2.0)
        advanceUntilIdle()

        assertEquals(listOf(fastNap), viewModel.uiState.value.cachedNapBoxList)
    }

    @Test
    fun `processCurrentLocation toggles isLoadingLocation`() = runTest(testDispatcher) {
        coEvery { getPlaceFromLocationUseCase(any(), any()) } coAnswers {
            delay(50)
            Result.success(Place(id = "1", name = "P"))
        }
        coEvery { getNearNapBoxesUseCase(any(), any()) } returns Result.success(emptyList())

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.processCurrentLocation(-11.0, -77.0)
        assertTrue(viewModel.uiState.value.isLoadingLocation)

        advanceUntilIdle()
        assertEquals(false, viewModel.uiState.value.isLoadingLocation)
    }

    @Test
    fun `saveSubscription ignores second call while first is in progress`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val registered = Subscription(subscriptionId = 1, firstName = "A", lastName = "B")
        coEvery { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) } coAnswers {
            delay(100)
            Result.success(RegisterSubscriptionResult.Registered(registered))
        }

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))

        viewModel.saveSubscription(facadePhotoFile)
        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        coVerify(exactly = 1) { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) }
    }

    @Test
    fun `loadScreenData auto selects single active core and hides selector`() = runTest(testDispatcher) {
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                coreDevices = listOf(
                    CatalogCoreDevice(id = 10, name = "Core-A", disabled = false),
                    CatalogCoreDevice(id = 99, name = "Disabled", disabled = true)
                )
            )
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val form = viewModel.uiState.value.registerSubscriptionForm
        assertEquals(sampleCoreDevice, form.selectedHostDevice)
        assertFalse(form.shouldShowHostDeviceSelector())
    }

    @Test
    fun `loadScreenData leaves host null when multiple active cores`() = runTest(testDispatcher) {
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                coreDevices = listOf(
                    CatalogCoreDevice(id = 10, name = "Core-A", disabled = false),
                    CatalogCoreDevice(id = 11, name = "Core-B", disabled = false)
                )
            )
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val form = viewModel.uiState.value.registerSubscriptionForm
        assertNull(form.selectedHostDevice)
        assertTrue(form.shouldShowHostDeviceSelector())
    }

    @Test
    fun `loadScreenData emits error when no active cores`() = runTest(testDispatcher) {
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                coreDevices = listOf(CatalogCoreDevice(id = 1, name = "Off", disabled = true))
            )
        )

        val events = mutableListOf<RegisterSubscriptionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertTrue(events[0] is RegisterSubscriptionUiEvent.Error)
        assertEquals(
            "No hay routers core disponibles",
            (events[0] as RegisterSubscriptionUiEvent.Error).message
        )

        job.cancel()
    }

    @Test
    fun `HostDeviceSelected updates selected host and clears error`() = runTest(testDispatcher) {
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                coreDevices = listOf(
                    CatalogCoreDevice(id = 10, name = "Core-A", disabled = false),
                    CatalogCoreDevice(id = 11, name = "Core-B", disabled = false)
                )
            )
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.onIntent(RegisterSubscriptionIntent.RegisterClick(facadePhotoFile))
        advanceUntilIdle()
        assertTrue(
            viewModel.uiState.value.registerSubscriptionForm.hostDeviceError != null
        )

        viewModel.onIntent(RegisterSubscriptionIntent.HostDeviceSelected(secondCoreDevice))
        advanceUntilIdle()

        val form = viewModel.uiState.value.registerSubscriptionForm
        assertEquals(secondCoreDevice, form.selectedHostDevice)
        assertNull(form.hostDeviceError)
    }

    @Test
    fun `RegisterClick fails validation when multiple cores and none selected`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                napBoxes = listOf(fiberNap()),
                onus = listOf(fiberOnu()),
                coreDevices = listOf(
                    CatalogCoreDevice(id = 10, name = "Core-A", disabled = false),
                    CatalogCoreDevice(id = 11, name = "Core-B", disabled = false)
                )
            )
        )

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))

        viewModel.onIntent(RegisterSubscriptionIntent.RegisterClick(facadePhotoFile))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.registerSubscriptionForm.hostDeviceError)
        coVerify(exactly = 0) { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) }
    }

    @Test
    fun `loadScreenData marks form offline when MikroTik is unreachable`() = runTest(testDispatcher) {
        offlineModeFlow.value = true

        viewModel.loadScreenData(null)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOfflineMode)
        assertTrue(viewModel.uiState.value.registerSubscriptionForm.requiresClientIpAddress)
    }

    @Test
    fun `ClientIpAddressChanged stores value on form`() = runTest(testDispatcher) {
        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.onIntent(RegisterSubscriptionIntent.ClientIpAddressChanged("10.1.1.20"))
        advanceUntilIdle()

        assertEquals("10.1.1.20", viewModel.uiState.value.registerSubscriptionForm.clientIpAddress)
    }

    @Test
    fun `saveSubscription sends clientIpAddress when offline`() = runTest(testDispatcher) {
        offlineModeFlow.value = true
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )
        viewModel.loadScreenData(null)
        advanceUntilIdle()

        val pending = PendingSubscription(
            localId = "local-1",
            clientRequestId = "client-1",
            subscriptionJson = "{}",
            createdAt = 1L
        )
        coEvery {
            registerSubscriptionUseCase(any(), any(), facadePhotoFile = any())
        } answers {
            assertEquals("192.168.25.10", firstArg<Subscription>().clientIpAddress)
            assertEquals("192.168.25.10", firstArg<Subscription>().ip)
            Result.success(RegisterSubscriptionResult.QueuedOffline(pending))
        }

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))
        viewModel.onIntent(RegisterSubscriptionIntent.ClientIpAddressChanged("192.168.25.10"))

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        coVerify(exactly = 1) { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) }
    }

    @Test
    fun `OnVlanChanged updates form vlan`() = runTest(testDispatcher) {
        viewModel.loadScreenData(null)
        advanceUntilIdle()

        viewModel.onIntent(RegisterSubscriptionIntent.OnVlanChanged("100"))

        assertEquals("100", viewModel.uiState.value.registerSubscriptionForm.vlan)
    }

    @Test
    fun `saveSubscription sends vlan 100 only for FIBER`() = runTest(testDispatcher) {
        val nap = NapBoxResponse(id = "n1", placeName = "P1", placeId = 1)
        val onu = Onu("b", "olt", "1", "t", "type", "pon", "p", "sn1")
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(napBoxes = listOf(fiberNap()), onus = listOf(fiberOnu()))
        )
        viewModel.loadScreenData(null)
        advanceUntilIdle()

        coEvery {
            registerSubscriptionUseCase(any(), any(), facadePhotoFile = any())
        } answers {
            assertEquals("100", firstArg<Subscription>().vlan)
            Result.success(RegisterSubscriptionResult.Registered(Subscription(subscriptionId = 1)))
        }

        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(samplePlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.NapBoxSelected(nap))
        viewModel.onIntent(RegisterSubscriptionIntent.OnuSelected(onu))
        viewModel.onIntent(RegisterSubscriptionIntent.OnVlanChanged("100"))

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        coVerify(exactly = 1) { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) }
    }

    @Test
    fun `saveSubscription sends vlan null when installation is not FIBER`() = runTest(testDispatcher) {
        val wirelessPlan = PlanResponse(
            id = "w1",
            name = "Wireless",
            price = 10.0,
            downloadSpeed = "50",
            uploadSpeed = "20",
            type = InstallationType.WIRELESS
        )
        coEvery { getRegistrationCatalogUseCase() } returns Result.success(
            sampleCatalog(
                plans = listOf(
                    CatalogPlan(
                        id = "p1",
                        name = "Plan",
                        price = 10.0,
                        downloadSpeed = "100",
                        uploadSpeed = "100",
                        type = "FIBER"
                    ),
                    CatalogPlan(
                        id = "w1",
                        name = "Wireless",
                        price = 10.0,
                        downloadSpeed = "50",
                        uploadSpeed = "20",
                        type = "WIRELESS"
                    )
                )
            )
        )
        viewModel.loadScreenData(null)
        advanceUntilIdle()

        coEvery {
            registerSubscriptionUseCase(any(), any(), facadePhotoFile = any())
        } answers {
            assertNull(firstArg<Subscription>().vlan)
            Result.success(RegisterSubscriptionResult.Registered(Subscription(subscriptionId = 2)))
        }

        viewModel.onIntent(RegisterSubscriptionIntent.InstallationTypeSelected(InstallationType.WIRELESS))
        viewModel.onIntent(RegisterSubscriptionIntent.FirstNameChanged("Juan"))
        viewModel.onIntent(RegisterSubscriptionIntent.LastNameChanged("Perez"))
        viewModel.onIntent(RegisterSubscriptionIntent.DniChanged("12345678"))
        viewModel.onIntent(RegisterSubscriptionIntent.AddressChanged("Calle larga 12345"))
        viewModel.onIntent(RegisterSubscriptionIntent.PhoneChanged("987654321"))
        viewModel.onIntent(RegisterSubscriptionIntent.PlanSelected(wirelessPlan))
        viewModel.onIntent(RegisterSubscriptionIntent.PlaceSelected(Place(id = "1", name = "P")))
        viewModel.onIntent(RegisterSubscriptionIntent.OnVlanChanged("100"))

        viewModel.saveSubscription(facadePhotoFile)
        advanceUntilIdle()

        coVerify(exactly = 1) { registerSubscriptionUseCase(any(), any(), facadePhotoFile = any()) }
    }
}
