package com.dscorp.ispadmin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FIndSubscriptionFragment : Fragment() {

    companion object {
        fun newInstance() = FIndSubscriptionFragment()
    }

    private lateinit var viewModel: FIndSubscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_subscription, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FIndSubscriptionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}