package com.dscorp.ispadmin.presentation.subscriptionfinder

sealed interface FindSubscriptionUiErrorState {
    class OnError(message: String?) : FindSubscriptionUiErrorState {

    }

}
