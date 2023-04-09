package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.*
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class RegisterEditRegisterSubscriptionViewModelTest : AutoCloseKoinTest() {
    private lateinit var repository: IRepository

    @Before
    fun before() {
        repository = Mockito.mock(IRepository::class.java)
    }

    @Test
    fun `when getFormData is called then should return FormDataFound sate`() = runBlocking {
        `when`(repository.getGenericDevices()).thenReturn(listOf())
        `when`(repository.getPlans()).thenReturn(listOf())
        `when`(repository.getPlaces()).thenReturn(listOf())
        `when`(repository.getNapBoxes()).thenReturn(listOf())
        `when`(repository.getTechnicians()).thenReturn(listOf())
        `when`(repository.getNetworkDeviceTypes()).thenReturn(listOf())
        `when`(repository.getCoreDevices()).thenReturn(listOf())

        val viewModel = RegisterSubscriptionViewModel(repository)
        viewModel.getFormData()

        val value = viewModel.registerSubscriptionUiState.getValueForTest()

        assertTrue(value is RegisterSubscriptionUiState.FormDataFound)
    }

    @Test
    fun `when getFormData call has error then should return FormDataError sate`() = runBlocking {
        Mockito.doThrow(NullPointerException("Error")).`when`(repository).getGenericDevices()
        val viewModel = RegisterSubscriptionViewModel(repository)
        viewModel.getFormData()

        val value = viewModel.registerSubscriptionUiState.getValueForTest()

        assertTrue(value is RegisterSubscriptionUiState.FormDataError)
    }
}
