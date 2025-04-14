package com.dscorp.ispadmin.presentation.ui.features.payment.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.entity.Payment
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

data class RegisterPaymentState(
    val isLoading: Boolean = false,
    val payment: Payment? = null,
    val electronicPayers: List<String> = emptyList(),
    val paymentMethod: String = "",
    val discountAmount: String = "",
    val discountReason: String = "",
    val showDiscountFields: Boolean = false,
    val errorMessages: Map<String, String> = emptyMap(),
    val isSuccess: Boolean = false,
    val electronicPayerName: String = ""
)

sealed interface RegisterPaymentEvent {
    data class PaymentMethodSelected(val method: String) : RegisterPaymentEvent
    data class DiscountAmountChanged(val amount: String) : RegisterPaymentEvent
    data class DiscountReasonChanged(val reason: String) : RegisterPaymentEvent
    data class ElectronicPayerNameChanged(val name: String) : RegisterPaymentEvent
    object RegisterPayment : RegisterPaymentEvent
    data class SetPayment(val payment: Payment) : RegisterPaymentEvent
    object ToggleDiscountFields : RegisterPaymentEvent
}

class RegisterPaymentViewModel(private val repository: IRepository) : ViewModel(), KoinComponent {
    private val _state = MutableStateFlow(RegisterPaymentState())
    val state: StateFlow<RegisterPaymentState> = _state.asStateFlow()

    private val _electronicPayerSearchFlow = MutableStateFlow("")
    
    val user = repository.getUserSession()

    init {
        observeElectronicPayerSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeElectronicPayerSearch() = viewModelScope.launch {
        _electronicPayerSearchFlow.debounce(300).collect {
            if (it.isEmpty() || it.length < 3) return@collect
            val response = repository.findPaymentByElectronicPayerName(it)
            // Handle response if needed
        }
    }

    fun getElectronicPayers() {
        viewModelScope.launch {
            _state.value.payment?.subscriptionId?.let { subscriptionId ->
                val payers = repository.getElectronicPayers(subscriptionId)
                _state.update { it.copy(electronicPayers = payers) }
            }
        }
    }

    fun onEvent(event: RegisterPaymentEvent) {
        when (event) {
            is RegisterPaymentEvent.PaymentMethodSelected -> {
                _state.update { 
                    it.copy(
                        paymentMethod = event.method,
                        errorMessages = it.errorMessages - "paymentMethod"
                    ) 
                }
            }
            is RegisterPaymentEvent.DiscountAmountChanged -> {
                _state.update { 
                    val errors = validateDiscountAmount(event.amount, it.payment?.amountToPay ?: 0.0)
                        ?.let { error -> it.errorMessages + ("discountAmount" to error) }
                        ?: it.errorMessages - "discountAmount"
                    
                    it.copy(
                        discountAmount = event.amount,
                        errorMessages = errors
                    ) 
                }
            }
            is RegisterPaymentEvent.DiscountReasonChanged -> {
                _state.update { 
                    it.copy(
                        discountReason = event.reason,
                        errorMessages = it.errorMessages - "discountReason"
                    ) 
                }
            }
            is RegisterPaymentEvent.ElectronicPayerNameChanged -> {
                _state.update { 
                    it.copy(
                        electronicPayerName = event.name
                    ) 
                }
            }
            is RegisterPaymentEvent.RegisterPayment -> {
                registerPayment()
            }
            is RegisterPaymentEvent.SetPayment -> {
                val payment = event.payment
                val showDiscountFields = payment.discountAmount != null && payment.discountAmount!! > 0
                
                _state.update { 
                    it.copy(
                        payment = payment,
                        showDiscountFields = showDiscountFields,
                        discountAmount = payment.discountAmount?.toString() ?: "",
                        discountReason = payment.discountReason ?: ""
                    )
                }
                
                getElectronicPayers()
            }
            is RegisterPaymentEvent.ToggleDiscountFields -> {
                _state.update {
                    it.copy(
                        showDiscountFields = !it.showDiscountFields,
                        // Si estamos desactivando los campos, limpiar los valores
                        discountAmount = if (it.showDiscountFields) "" else it.discountAmount,
                        discountReason = if (it.showDiscountFields) "" else it.discountReason,
                        // Eliminar posibles errores de descuento
                        errorMessages = it.errorMessages - "discountAmount" - "discountReason"
                    )
                }
            }
        }
    }

    private fun validateDiscountAmount(amount: String, maxAmount: Double): String? {
        if (amount.isEmpty()) return null
        
        return try {
            val amountDouble = amount.toDouble()
            if (amountDouble > maxAmount) {
                "El descuento no puede ser mayor a la deuda"
            } else if (amountDouble < 0) {
                "El descuento no puede ser negativo"
            } else {
                null
            }
        } catch (e: Exception) {
            "Monto de descuento inválido"
        }
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()
        val currentState = _state.value
        
        if (currentState.paymentMethod.isEmpty()) {
            errors["paymentMethod"] = "Debe seleccionar un método de pago"
        }
        
        if (currentState.paymentMethod != "Efectivo" && currentState.electronicPayerName.isEmpty()) {
            errors["electronicPayerName"] = "Debe ingresar el nombre del pagador"
        }
        
        currentState.discountAmount.takeIf { it.isNotEmpty() }?.let {
            validateDiscountAmount(it, currentState.payment?.amountToPay ?: 0.0)?.let { error ->
                errors["discountAmount"] = error
            }
        }
        
        _state.update { it.copy(errorMessages = errors) }
        return errors.isEmpty()
    }

    private fun registerPayment() {
        if (!validateForm()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val currentState = _state.value
                currentState.payment?.let { originalPayment ->
                    val paymentToRegister = Payment(
                        id = originalPayment.id,
                        discountAmount = currentState.discountAmount.takeIf { it.isNotEmpty() && currentState.showDiscountFields }?.toDoubleOrNull() ?: 0.0,
                        discountReason = currentState.discountReason.takeIf { it.isNotEmpty() && currentState.showDiscountFields },
                        method = currentState.paymentMethod,
                        responsibleId = user?.id ?: 0,
                        electronicPayerName = currentState.electronicPayerName.takeIf { it.isNotEmpty() }
                    )
                    
                    val response = repository.registerPayment(paymentToRegister)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessages = it.errorMessages + ("general" to (e.message ?: "Error al registrar pago"))
                    ) 
                }
            }
        }
    }
}
