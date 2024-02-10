package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cleanarchitecture.domain.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResume

val filters = listOf(
    SubscriptionFilter.BY_NAME(),
    SubscriptionFilter.BY_DOCUMENT(),
    SubscriptionFilter.BY_DATE()
)

@Composable
fun SubscriptionFinderScreen(
    subscriptions: List<SubscriptionResume>,
    onSearch: (SubscriptionFilter) -> Unit = {},
    onMenuItemSelected: (menuItem: SubscriptionMenu, subscriptionId: Int) -> Unit = { _, _ -> }
) {
    var lastScrollOffset by remember { mutableStateOf(1) }
    var scrollingUp by remember { mutableStateOf(0) }

    val animatedScrollingUp by animateDpAsState(
        targetValue = if (scrollingUp > 0) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing), label = ""
    )

    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = scrollState) {
        snapshotFlow { scrollState.firstVisibleItemScrollOffset }.collect { offset ->
            scrollingUp = if (offset < lastScrollOffset) 100 else 0
            lastScrollOffset = offset
        }
    }

    SubscriptionFinderContent(
        animatedScrollingUp = animatedScrollingUp,
        onSearch = onSearch,
        subscriptions = subscriptions,
        scrollState = scrollState,
        onMenuItemSelected = onMenuItemSelected
    )
}

@Composable
fun SubscriptionFinderContent(
    animatedScrollingUp: Dp,
    onSearch: (SubscriptionFilter) -> Unit,
    subscriptions: List<SubscriptionResume>,
    scrollState: LazyListState,
    onMenuItemSelected: (menuItem: SubscriptionMenu, subscriptionId: Int) -> Unit
) {
    var filtersVisible by remember { mutableStateOf(true) }
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(top = animatedScrollingUp)
) {
    AnimatedVisibility(visible = filtersVisible) {
        SubscriptionFinderFilters(
            onSearch = { onSearch(it) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = if (filtersVisible) 0.dp else -animatedScrollingUp)
        )
    }
        SubscriptionList(
        subscriptions = subscriptions,
        scrollState = scrollState,
        onMenuItemSelected = onMenuItemSelected,
    )
}

  LaunchedEffect(key1 = scrollState) {
    var lastScrollOffset = scrollState.firstVisibleItemScrollOffset
    snapshotFlow { scrollState.firstVisibleItemScrollOffset }.collect { offset ->
        val newFiltersVisible = offset <= lastScrollOffset
        if (newFiltersVisible != filtersVisible) {
            filtersVisible = newFiltersVisible
        }
        lastScrollOffset = offset
    }
}
}


fun Int.toDp(context: Context): Dp {
    return this.dp / context.resources.displayMetrics.density
}

@Preview(showSystemUi = true)
@Composable
private fun SubscriptionFinderPreview() {
    MaterialTheme {
        SubscriptionFinderScreen(subscriptions = generateMockSubscriptions())
    }
}

fun generateMockSubscriptions(): List<SubscriptionResume> {
    val mockList = mutableListOf<SubscriptionResume>()
    for (i in 1..5) {
        val subscriptionResume = SubscriptionResume(
            id = i,
            planName = "Plan $i",
            customerName = "Cliente $i",
            antiquity = "${i} año(s)",
            qualification = "${i} estrella(s)",
            placeName = "Lugar $i",
            ics = "12345$i",
            lastPaymentDate = "12/12/2${i + 1}",
            pendingInvoicesQuantity = i,
            totalDebt = 100.0 * i,
            ipAddress = "192.168.1.$i",
            customer = CustomerData(
                name = "Nombre $i",
                lastName = "Apellido $i",
                dni = "4827183$i",
                place = "Lugar $i",
                address = "Dirección $i",
                phone = "98765432$i",
                email = "cliente$i@gmail.com",
                subscriptionId = i
            )
        )
        mockList.add(subscriptionResume)
    }
    return mockList
}
