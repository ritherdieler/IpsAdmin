package com.dscorp.ispadmin.presentation.ui.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.dscorp.components.ProgressFullScreenDialogFragment
import com.dscorp.ispadmin.presentation.extension.analytics.sendScreen
import com.dscorp.ispadmin.presentation.extension.showCurrentSimpleName
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

abstract class BaseFragment<T, U : ViewDataBinding> : Fragment() {
    protected val firebaseAnalytics: FirebaseAnalytics by inject()
    protected abstract val viewModel: BaseViewModel<T>
    protected abstract val binding: U
    protected abstract fun handleState(state: T)

    private val fullScreenProgressDialog: ProgressFullScreenDialogFragment by lazy {
        ProgressFullScreenDialogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        onViewReady()
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.error?.let { error -> showErrorDialog(error.message?:"") }
            uiState.loading?.let { isLoading -> onLoading(isLoading) }
            uiState.uiState?.let { handleState(it) }
        }
        return binding.root
    }

    protected open fun onLoading(isLoading: Boolean) {
        if (isLoading) fullScreenProgressDialog.show(
            childFragmentManager,
            "BaseFragmentFullScreenProgress"
        )
        else fullScreenProgressDialog.dismiss()
    }

    protected open fun onViewReady() {}


    override fun onResume() {
        super.onResume()
        showCurrentSimpleName()
        firebaseAnalytics.sendScreen(this::class.java.simpleName)
    }
}
