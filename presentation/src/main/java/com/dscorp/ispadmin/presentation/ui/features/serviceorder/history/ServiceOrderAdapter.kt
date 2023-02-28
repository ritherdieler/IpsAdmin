package com.dscorp.ispadmin.presentation.ui.features.serviceorder.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemNapBoxesListBinding
import com.dscorp.ispadmin.databinding.ItemServicesOrderBinding
import com.dscorp.ispadmin.presentation.ui.features.napboxeslist.OnItemClickListener
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse

class ServiceOrderAdapter(val listener: OnItemServiceOrderClickListener): ListAdapter<ServiceOrderResponse, ServiceOrderAdapter.ServiceOrderAdapterViewHolder>(
    ServiceOrderDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceOrderAdapterViewHolder {
        val binding =
            ItemServicesOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceOrderAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceOrderAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ServiceOrderAdapterViewHolder(private val binding: ItemServicesOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceorder: ServiceOrderResponse) {
            binding.serviceOrderList = serviceorder
            binding.executePendingBindings()
            binding.btnMenu.setOnClickListener {
                listener.onServiceOrderPopupButtonSelected(serviceorder, it)
            }
        }
    }
}

private class ServiceOrderDiffCallback : DiffUtil.ItemCallback<ServiceOrderResponse>() {
    override fun areItemsTheSame(oldItem: ServiceOrderResponse, newItem: ServiceOrderResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ServiceOrderResponse, newItem: ServiceOrderResponse): Boolean {
        return oldItem == newItem
    }
}
