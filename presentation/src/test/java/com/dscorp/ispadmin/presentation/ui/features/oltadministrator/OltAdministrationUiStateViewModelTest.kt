package com.dscorp.ispadmin.presentation.ui.features.oltadministrator

import com.example.data2.data.repository.IRepository
import com.example.data2.data.response.AdministrativeOnuResponse
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
class OltAdministrationUiStateViewModelTest {

    private lateinit var viewModel: OltAdministrationViewModel
    private lateinit var repository: IRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = Mockito.mock(IRepository::class.java)
        viewModel = OltAdministrationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getOnuBySn should emit Success`() = runTest(testDispatcher) {
        //GIVEN
        val onuMock = Mockito.mock(AdministrativeOnuResponse::class.java)
        Mockito.`when`(repository.getOnuBySn("123")).thenAnswer {
            runBlocking {
                return@runBlocking onuMock
            }
        }
        val states = mutableListOf<OltAdministrationUiState>()

        //WHEN
        backgroundScope.launch {
            viewModel.uiState.onEach { states.add(it) }.collect {}
        }
        viewModel.getOnuBySn("123")

        //THEN
        assertEquals(states[0], OltAdministrationUiState.Empty)
        assertEquals(states[1], OltAdministrationUiState.Loading)
        assertEquals(states[2], OltAdministrationUiState.GetOnuSuccess(onuMock))

    }

}