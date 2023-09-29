package com.dscorp.ispadmin.presentation.ui.features.migration

import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MigrationViewModelTest {

    private lateinit var repository: IRepository
    private lateinit var migrationViewModel: MigrationViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = Mockito.mock(IRepository::class.java)
        migrationViewModel = MigrationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when migration is done then should return Success state`() = runTest(dispatcher) {
        // Given
        val onu = Mockito.mock(Onu::class.java)
        val subscription = Mockito.mock(SubscriptionResponse::class.java)
        Mockito.`when`(repository.doMigration(1, 2,onu)).thenAnswer {
            runBlocking { return@runBlocking subscription }
        }

        val collectedStates = mutableListOf<MigrationUiState>()

        // When
        backgroundScope.launch {
            migrationViewModel.uiState.onEach { collectedStates.add(it) }.collect {}
        }
        migrationViewModel.doMigration(1, 2,onu)

        // Then
        assertEquals(MigrationUiState.Empty, collectedStates[0])
        assertEquals(MigrationUiState.Loading, collectedStates[1])
        assertEquals(MigrationUiState.Success(subscription), collectedStates[2])
    }

    @Test
    fun `doMigration sets uiState to Loading and then Error when repository call fails`() =
        runTest {
            // Given
            val onu = Mockito.mock(Onu::class.java)
            val subscriptionId = 1
            val planId = 2
            val exception = NullPointerException("error")
            Mockito.`when`(repository.doMigration(subscriptionId, planId, onu)).thenAnswer {
                runBlocking { return@runBlocking exception }
            }
            val collectedStates = mutableListOf<MigrationUiState>()

            // When
            backgroundScope.launch(dispatcher) {
                migrationViewModel.uiState.onEach { collectedStates.add(it) }.collect {}
            }
            migrationViewModel.doMigration(subscriptionId, planId, onu)

            // Then
            assertEquals(MigrationUiState.Empty, collectedStates[0])
            assertEquals(MigrationUiState.Loading, collectedStates[1])
            assertEquals(MigrationUiState.Error(exception), collectedStates[2])

        }

    @Test
    fun `getPlans should return success`() = runTest {
        // Given
        val plan = Mockito.mock(PlanResponse::class.java)
        val plans = listOf(plan)
        Mockito.`when`(repository.getPlans()).thenAnswer {
            runBlocking { return@runBlocking plans }
        }
        val collectedStates = mutableListOf<MigrationUiState>()

        // When
        backgroundScope.launch(dispatcher) {
            migrationViewModel.uiState.onEach {collectedStates.add(it)}.collect {}
        }
        migrationViewModel.getPlans()

        // Then
        assertEquals(MigrationUiState.Empty, collectedStates[0])
        assertEquals(MigrationUiState.Loading, collectedStates[1])
        assertEquals(MigrationUiState.FormDataReady(plans, unconfirmedOnus), collectedStates[2])
    }

}