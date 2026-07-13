package com.dscorp.ispadmin.presentation.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.User
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

class ProfileViewModel : ViewModel(), KoinComponent {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    private val observabilityClient: ObservabilityClient by KoinJavaComponent.inject(ObservabilityClient::class.java)

    private companion object {
        const val OBS_FEATURE = "profile"
        const val OBS_SCREEN = "profile"
    }

    // Estado de la UI usando UDF
    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = repository.getUserSession()
                if (user != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            user = user
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "No se pudo cargar el perfil"
                        )
                    }
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cargar perfil de usuario",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_profile")
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
    
    fun logOut() {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.logout",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN)
            )
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.clearUserSession()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isLoggedOut = true
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cerrar sesión",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "logout")
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cerrar sesión"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

// Estado de la UI siguiendo UDF
data class MyProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)
