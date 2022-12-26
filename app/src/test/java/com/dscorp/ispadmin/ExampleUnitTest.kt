package com.dscorp.ispadmin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.*
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.repository.IRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = KoinAppForInstrumentation::class)
class ExampleUnitTest : KoinTest {

    val repositoryMock: IRepository by inject(IRepository::class.java)
    val mockViewModel: SubscriptionsListViewModel by viewmodel()

    @get:Rule
    val instantExcecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }


//    @get:Rule
//    val koinTestRule = MyKoinTestRule(
//        modules = listOf(instrumentedTestModule)
//    )

    @Before
    fun setup() {

        loadKoinModules(module {
            single { repository }
            single<SubscriptionsListViewModel> { viewModel }
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should inject my components`() = runTest {

//        loadKoinModules(instrumentedTestModule)

        val scenario = launchFragmentInContainer<SubscriptionsListFragment>(themeResId = R.style.Theme_IspAdminAndroid)

        scenario.onFragment { fragment ->
            launch {

//                `when`(fragment.viewModel.repository.getSubscriptions()).thenReturn(subscriptionListMock)
                assertEquals(mockViewModel, fragment.viewModel.repository)
//                verify(mockViewModel.repository).getSubscriptions()
            }
        }

    }

}