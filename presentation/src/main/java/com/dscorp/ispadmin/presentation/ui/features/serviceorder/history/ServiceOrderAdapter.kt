package com.dscorp.ispadmin.presentation.ui.features.serviceorder.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemServicesOrderBinding
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse

class ServiceOrderAdapter : RecyclerView.Adapter<ServiceOrderAdapter.ServicesOrderViewHolder>() {
    private var servicesOrderList: List<ServiceOrderResponse> = emptyList()

    fun submitList(servicesOrder: List<ServiceOrderResponse>) {
        this.servicesOrderList = servicesOrder
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesOrderViewHolder {
        return ServicesOrderViewHolder(ItemServicesOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ServicesOrderViewHolder(private val binding: ItemServicesOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceOrder: ServiceOrderResponse) {
            binding.tvSeeLongitude.text = serviceOrder.longitude.toString()
            binding.tvSeeLatitude.text = serviceOrder.latitude.toString()
            binding.tvSeeId.text = serviceOrder.id.toString()
        }
    }

    override fun onBindViewHolder(holder: ServicesOrderViewHolder, position: Int) {
        holder.bind(servicesOrderList[position])
    }

    override fun getItemCount(): Int {
        return servicesOrderList.size
    }
}
