package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.databinding.FragmentCreateSupportTicketBinding
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.MyAutoCompleteAdapter
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketState
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketViewModel
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionFastSearchResponse
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateSupportTicketFragment :
    BaseFragment<SupportTicketState, FragmentCreateSupportTicketBinding>() {
    override val viewModel: SupportTicketViewModel by viewModel()
    override val binding by lazy { FragmentCreateSupportTicketBinding.inflate(layoutInflater) }
    private val subscriptionList = mutableListOf<SubscriptionFastSearchResponse>()
    private val autocompleteAdapter by lazy {
        MyAutoCompleteAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            subscriptionList
        )
    }

    private var selectedSubscription: SubscriptionFastSearchResponse? = null
    private var selectedPlace: PlaceResponse? = null

    override fun handleState(state: SupportTicketState) {

        when (state) {
            is SupportTicketState.TicketCreated -> {
                showSuccessDialog("Ticket Creado Correctamente") {
                    findNavController().popBackStack()
                }
            }

            is SupportTicketState.SearchSubscriptionResult -> {
                autocompleteAdapter.clear()
                autocompleteAdapter.addAll(state.response)
                autocompleteAdapter.notifyDataSetChanged()
            }

            is SupportTicketState.FormError -> {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }

    }

    // Declaración de un Handler para realizar la búsqueda después de un retraso
    private val searchHandler = Handler(Looper.getMainLooper())
    override fun onViewReady(savedInstanceState: Bundle?) {

        binding.ticketTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioButtonIsClient.id -> {
                    binding.locationTextInputLayout.visibility = View.GONE
                    binding.subscriptionIdTextInputLayout.visibility = View.VISIBLE
                    selectedPlace = null
                }

                binding.radioButtonIsNotClient.id -> {
                    binding.subscriptionIdTextInputLayout.visibility = View.GONE
                    binding.locationTextInputLayout.visibility = View.VISIBLE
                    selectedSubscription = null
                }
            }
        }

        binding.spnLocation.onItemClickListener =
            AdapterView.OnItemClickListener { parent, vew, position, id ->
                selectedPlace =
                    parent.getItemAtPosition(position) as PlaceResponse
            }

        observeFlows()


        binding.spnCategory.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.categories
            )
        )


        binding.subscriptionIdAutoCompleteTextView.doOnTextChanged { text, start, before, count ->

            if (!text.isNullOrEmpty()) {
                // Cancelar la búsqueda programada previa, si existe
                searchHandler.removeCallbacksAndMessages(null)

                // Programar una nueva búsqueda después de un retraso de 500 ms (ajusta este valor según tus necesidades)
                searchHandler.postDelayed({
                    viewModel.findSubscriptionByNames(text.toString())
                }, 500)
            }

        }

        binding.subscriptionIdAutoCompleteTextView.setAdapter(autocompleteAdapter)
        binding.subscriptionIdAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, vew, position, id ->
                selectedSubscription =
                    parent.getItemAtPosition(position) as SubscriptionFastSearchResponse
            }



        binding.registerButton.setOnClickListener {
            val supportTicket = createTicket()
            viewModel.createTicket(supportTicket)
        }
    }

    private fun observeFlows() = lifecycleScope.launch {
        viewModel.placesFlow.collect {
            binding.spnLocation.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    it
                )
            )
        }
    }

    private fun createTicket() = AssistanceTicketRequest(
        phone = binding.phoneEditText.text.toString(),
        category = binding.spnCategory.text.toString(),
        description = binding.descriptionEditText.text.toString(),
        subscriptionId = selectedSubscription?.id,
        customerName = binding.subscriptionIdAutoCompleteTextView.text.toString(),
        placeName = selectedPlace?.name
    )

}
