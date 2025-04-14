package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.presentation.theme.MyTheme
import com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail.compose.SubscriptionDetailForm

class SubscriptionDetailFragment :
    Fragment() {

    val args: SubscriptionDetailFragmentArgs by navArgs()

    val binding by lazy { FragmentSubscriptionDetailBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.root.setContent {
            MyTheme {
                SubscriptionDetailForm(args.subscriptionId)
            }
        }

        return binding.root
    }
}
