package com.dscorp.ispadmin.presentation.napboxeslist.napboxdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapBoxDetailBinding
import com.dscorp.ispadmin.domain.entity.NapBox

class NapBoxDetailsFragment : Fragment() {

    private val args: NapBoxDetailsFragmentArgs by navArgs()
    lateinit var binding: FragmentNapBoxDetailBinding
    lateinit var napbox: NapBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        napbox = args.napBox
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_nap_box_detail, null, true)
        binding.tvNapBox.text = napbox.code
        return binding.root
    }
}