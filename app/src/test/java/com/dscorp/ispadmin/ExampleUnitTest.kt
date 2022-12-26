package com.dscorp.ispadmin

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.*
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.Repository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
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

    private val repositoryMock: IRepository by inject(Repository::class.java)
    private val viewModel: SubscriptionsListViewModel by inject(SubscriptionsListViewModel::class.java)
    private lateinit var scenario: FragmentScenario<SubscriptionsListFragment>

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `GIVEN FRAGMENT WHEN SUBSCRIPTION LIST IS NOT EMPTY THEN RECYCLERVIEW SHOULD BE SHOWN`() = runTest {
        `when`(repositoryMock.getSubscriptions()).thenReturn(subscriptionListMock)

        scenario = launchFragmentInContainer(themeResId = R.style.Theme_IspAdminAndroid)
        scenario.onFragment {
            launch {
                viewModel.responseLiveData.observeForever {
                    onView(withId(R.id.rvSubscription)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                }
            }
        }

    }

    @Test
    fun `GIVEN FRAGMENT WHEN SUBSCRIPTION LIST IS EMPTY THEN RECYCLERVIEW SHOULD BE HIDDEN`() = runTest {
        `when`(repositoryMock.getSubscriptions()).thenReturn(emptyList())

        scenario = launchFragmentInContainer(themeResId = R.style.Theme_IspAdminAndroid)
        scenario.onFragment {
            launch {
                viewModel.responseLiveData.observeForever {
                    onView(withId(R.id.rvSubscription))
                        .check(matches(not(isDisplayed())))
                }
            }
        }

    }

}