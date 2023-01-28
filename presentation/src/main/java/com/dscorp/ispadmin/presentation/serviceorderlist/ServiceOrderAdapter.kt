package com.dscorp.ispadmin.presentation.serviceorderlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemServicesOrderBinding
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder

class ServiceOrderAdapter : ListAdapter<ServiceOrder, ServiceOrderAdapter.ServiceOrderAdapterViewHolder>(ServiceOrderDiffCallback()) {

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
        fun bind(serviceOrder: ServiceOrder) {
            binding.serviceOrderList = serviceOrder
            binding.executePendingBindings()
        }
    }
}



private class ServiceOrderDiffCallback : DiffUtil.ItemCallback<ServiceOrder>() {
    override fun areItemsTheSame(oldItem: ServiceOrder, newItem: ServiceOrder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ServiceOrder, newItem: ServiceOrder): Boolean {
        return oldItem == newItem
    }
}