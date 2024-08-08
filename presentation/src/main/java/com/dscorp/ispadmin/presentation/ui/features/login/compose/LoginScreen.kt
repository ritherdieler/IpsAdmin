package com.dscorp.ispadmin.presentation.ui.features.login.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyConfirmDialog
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.login.CheckVersionState.*
import com.dscorp.ispadmin.presentation.ui.features.login.LoginState
import com.dscorp.ispadmin.presentation.ui.features.login.LoginViewModel
import com.dscorp.ispadmin.presentation.ui.features.migration.Loader
import com.example.cleanarchitecture.domain.domain.entity.User
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onCreatedAccountClicked: () -> Unit = {},
    onLoginSuccess: (User) -> Unit = {},
    onAcceptUpdate: () -> Unit = {},
    onNoUpdate: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel()
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAppVersion()
    }

    val loginState by viewModel.loginRequestFlow.collectAsState()

    val checkVersionState by viewModel.checkVersionFlow.collectAsState()

    when (val response = checkVersionState) {
        is CheckVersionSuccess -> {
            if (!response.forceUpdate) {
                onNoUpdate()
                Login(loginState = loginState,
                    onLoginClicked = { loginData ->
                        viewModel.doLogin(loginData)
                    },
                    onCreateAccountClicked = {
                        onCreatedAccountClicked()
                    })
            } else {
                MyConfirmDialog(
                    title = "Hay una nueva versión disponible",
                    body = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Es necesario actualizar la aplicación para continuar",
                            textAlign = TextAlign.Center
                        )
                    },
                    onAccept = {
                        onAcceptUpdate()
                    },
                    onDismissRequest = {}
                )
            }

        }

        is Error -> {
            MyCustomDialog {
                Text(text = "No se pudo verificar la versión de la aplicación")
            }
        }

        Loading -> {
            Loader()
        }
    }



    if (loginState is LoginState.LoginSuccess) {
        onLoginSuccess((loginState as LoginState.LoginSuccess).data)
    }

}
