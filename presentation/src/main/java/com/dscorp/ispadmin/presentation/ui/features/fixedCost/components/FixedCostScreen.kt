package com.dscorp.ispadmin.presentation.ui.features.fixedCost.components

import MyTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyConfirmDialog
import com.dscorp.ispadmin.presentation.ui.features.fixedCost.FixedCostViewModel
import com.dscorp.ispadmin.presentation.ui.features.fixedCost.SaveFixedCostState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FixedCostScreen(
    modifier: Modifier = Modifier,
    viewModel: FixedCostViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = Unit) { viewModel.getAllFixedCosts() }

    val saveFixedCostFlow by viewModel.saveFixedCostFlow.collectAsState()

    val fixedCosts by viewModel.getAllFixedCostsFlow.collectAsState()

    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Gastos fijos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        FixedCostList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            fixedCosts = fixedCosts
        )
        HorizontalDivider(modifier = Modifier.padding(16.dp))

        RegisterFixedCostForm(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onSave = {
                viewModel.saveFixedCost(it)
            }
        )
    }
    LaunchedEffect(key1 = saveFixedCostFlow) {
        when (saveFixedCostFlow) {
            SaveFixedCostState.Error -> {}
            SaveFixedCostState.Success -> {
                showDialog = true
            }

            SaveFixedCostState.Loading -> {

            }
        }
    }


    if (showDialog) {
        MyConfirmDialog(title = "", body = {
            Text(text = saveFixedCostFlow.message ?: "")
        }, onDismissRequest = {
            showDialog = false
        })
    }
}


@Preview(showBackground = true)
@Composable
private fun FixedCostPreview() {
    MyTheme {
        FixedCostScreen()
    }
}