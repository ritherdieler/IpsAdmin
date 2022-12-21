package com.dscorp.ispadmin

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.model.Subscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest : AutoCloseKoinTest() {

    lateinit var mockVm: SubscriptionsListViewModel
    lateinit var repositoryMock: IRepository

    val emptyList = emptyList<Subscription>()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    @Before
    fun setup() {

        repositoryMock = mock(IRepository::class.java)
        loadKoinModules(module {
            single { repositoryMock }
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

//    // Lazy inject property
//    val repostiroy: IRepository by inject()

    @Test
    fun `should inject my components`() = runTest {
        val scope = this
        Mockito.`when`(repositoryMock.getSubscriptions()).thenReturn(subscriptionListMock)
        mockVm = SubscriptionsListViewModel(get())

        loadKoinModules(module {
            viewModel { mockVm }
        })
/*        loadKoinModules(
            listOf(
                module {
                    single<IRepository> { Repository(get()) }
                })
        )
        val repositoryMock = declareMock<IRepository>()
        val subscriptionListViewModelMock = declareMock<SubscriptionsListViewModel>()
        val liveData = MutableLiveData<>
        val responseLiveData = MutableLiveData<SubscriptionsListResponse>()
        doReturn(responseLiveData).`when`(subscriptionListViewModelMock).responseLiveData
        Mockito.`when`(repositoryMock.getSubscriptions()).thenReturn(subscriptionListMock)*/
        val scenario = launchFragmentInContainer<SubscriptionsListFragment>(themeResId = R.style.Theme_IspAdminAndroid)
        scenario.withFragment {
            val viewmodel = this.viewModel
            scope.launch {
                Mockito.verify(repositoryMock).getSubscriptions()
                viewmodel.responseLiveData.observeForever {
                    val subscriptions = (it as SubscriptionsListResponse.OnSubscriptionFound).subscriptions
                    assertEquals(subscriptionListMock, subscriptions)
                    Espresso.onView(ViewMatchers.withId(R.id.rvSubscription))
                        .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility
                            .VISIBLE)))
                }
            }
        }
    }
}