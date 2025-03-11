package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LoaderDialog() {
    MyCustomDialog(modifier = Modifier.size(175.dp), cancelable = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            Loader(modifier = Modifier.align(Alignment.Center))
        }
    }
}