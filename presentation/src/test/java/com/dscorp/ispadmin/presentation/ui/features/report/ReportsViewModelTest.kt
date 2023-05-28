package com.dscorp.ispadmin.presentation.ui.features.report

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.getValueForTest
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class ReportsViewModelTest : AutoCloseKoinTest() {

    lateinit var repository: IRepository

    @Before
    fun setUp() {
        repository = Mockito.mock()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `when downloadDebtorsDocument is called, then should emit DebtorsDocument state`() =
        runBlocking {
            // Given
            Mockito.`when`(repository.downloadDebtorWithActiveSubscriptionsDocument()).thenReturn(
                DownloadDocumentResponse(
                    name = "",
                    type = "",
                    base64 = ""
                )
            )
            val viewModel = ReportsViewModel(repository)

            // When
            viewModel.downloadDebtorWithActiveSubscriptionsDocument()
            val value = viewModel.uiStateLiveData.getValueForTest()

            // Then
            assertTrue(value is ReportsUiState.DebtorsDocument)
        }

    @Test
    fun `when downloadDebtorsDocument call has error then should emit Error state`() =
        runBlocking {
            // Given
            Mockito.`when`(repository.downloadDebtorWithActiveSubscriptionsDocument()).thenThrow(NullPointerException())
            val viewModel = ReportsViewModel(repository)

            // When
            viewModel.downloadDebtorWithActiveSubscriptionsDocument()
            val value = viewModel.uiStateLiveData.getValueForTest()

            // Then
            assertTrue(value is ReportsUiState.DebtorsDocumentError)
        }
}
