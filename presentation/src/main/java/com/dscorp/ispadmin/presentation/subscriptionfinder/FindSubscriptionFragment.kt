package com.dscorp.ispadmin.presentation.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentFindSubscriptionBinding
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject

class FindSubscriptionFragment : Fragment(), SelectableSubscriptionListener {

    private val viewModel: FindSubscriptionViewModel by inject()

    private val binding: FragmentFindSubscriptionBinding by lazy {
        FragmentFindSubscriptionBinding.inflate(layoutInflater)
    }

    private val adapter = FindSubscriptionAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.findSubscriptionRecyclerView.adapter = adapter
        binding.btnFind.setOnClickListener {
            val dni = binding.findSubscriptionEditText.text.toString()
            viewModel.findSubscription(dni)
        }

        observeUiState()

        return binding.root
    }

    private fun observeUiState() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is FindSubscriptionUiState.OnSubscriptionFound -> adapter.submitList(it.subscriptions)
                is FindSubscriptionUiState.OnError -> showMaterialDialog(it.message)
            }
        }
    }

    private fun showMaterialDialog(message: String?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onSubscriptionSelected(subscription: SubscriptionResponse, view: View) {

        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.btn_register_payment -> {
                        findNavController().navigate(
                            FindSubscriptionFragmentDirections.findSubscriptionToRegisterPayment(
                                subscription
                            )
                        )
                        true
                    }
    /*                R.id.btn_show_payment_history -> {
                        findNavController().navigate(
                            FindSubscriptionFragmentDirections.findSubscriptionToShowPaymentHis(
                                subscription
                            )
                        )
                        true
                    }*/
                    else -> false
                }


            }
            inflate(R.menu.subscription_menu)
            show()
        }



    }

}