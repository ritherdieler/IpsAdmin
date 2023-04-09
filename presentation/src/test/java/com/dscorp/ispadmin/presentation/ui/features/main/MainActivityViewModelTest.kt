package com.dscorp.ispadmin.presentation.ui.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.example.cleanarchitecture.domain.domain.entity.User
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class MainActivityViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: IRepository = Mockito.mock(IRepository::class.java)
    private lateinit var viewModel: MainActivityViewModel

    // for tests flows consult https://developer.android.com/kotlin/flow/test
    @Test
    fun `when user session exists then emit user for analytics `() = runTest {
        // Given
        val user = User(
            id = 123,
            name = "sergio",
            lastName = "carrillo diestra",
            type = 2,
            username = "dscorp",
            password = "",
            verified = true
        )
        Mockito.`when`(repository.getUserSession()).thenReturn(user)

        // When
        viewModel = MainActivityViewModel(repository)

        // Then
        assertTrue(viewModel.uiState.value is MainActivityUiState.UserSessionsFound)
    }
}
