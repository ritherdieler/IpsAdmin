package com.dscorp.ispadmin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.*
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.domain.repository.IRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = KoinAppForInstrumentation::class)
class ExampleUnitTest {

    private val repositoryMock: IRepository by inject(IRepository::class.java)
    private val viewModel: SubscriptionsListViewModel by inject(SubscriptionsListViewModel::class.java)
    private lateinit var scenario: FragmentScenario<SubscriptionsListFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() = runBlocking {

    }

    @After
    fun tearDown() {
        stopKoin()
    }


    @Test
    fun  `GIVEN FRAGMENT WHEN SUBSCRIPTION LIST IS NOT EMPTY THEN RECYCLERVIEW SHOULD BE SHOWN`() = runTest {
        `when`(repositoryMock.getSubscriptions()).thenReturn(subscriptionListMock)
//        `when`(repositoryMock.getSubscriptions()).thenReturn(emptyList())


        scenario = launchFragmentInContainer(themeResId = R.style.Theme_IspAdminAndroid)
        scenario.onFragment {
            launch {

                viewModel.responseLiveData.observeForever { response ->
                    onView(withId(R.id.rvSubscription))
                        .check(matches(isDisplayed()))
                }

            }
        }

    }

    @Test
    fun `GIVEN FRAGMENT WHEN SUBSCRIPTION LIST IS EMPTY THEN RECYCLERVIEW SHOULD BE HIDDEN`() = runTest {
        `when`(repositoryMock.getSubscriptions()).thenReturn(emptyList())
//        `when`(repositoryMock.getSubscriptions()).thenReturn(subscriptionListMock)

        scenario = launchFragmentInContainer(themeResId = R.style.Theme_IspAdminAndroid)
        scenario.onFragment {
            launch {

                viewModel.responseLiveData.observeForever { response ->
                    onView(withId(R.id.rvSubscription))
                        .check(matches(not(isDisplayed())))
                }

            }
        }

    }

}