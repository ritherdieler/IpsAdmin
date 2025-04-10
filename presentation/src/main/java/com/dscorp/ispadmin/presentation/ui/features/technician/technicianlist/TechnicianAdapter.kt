package com.dscorp.ispadmin.presentation.ui.features.technician.technicianlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemTechnicianListBinding
import com.example.cleanarchitecture.domain.entity.Technician

class TechnicianAdapter : ListAdapter<Technician, TechnicianAdapter.TechnicianListAdapterViewHolder>(
    TechnicianListDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicianListAdapterViewHolder {
        val binding =
            ItemTechnicianListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TechnicianListAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TechnicianListAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TechnicianListAdapterViewHolder(private val binding: ItemTechnicianListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(technician: Technician) {
            binding.technicianList = technician
            binding.executePendingBindings()
        }
    }
}

private class TechnicianListDiffCallback : DiffUtil.ItemCallback<Technician>() {
    override fun areItemsTheSame(oldItem: Technician, newItem: Technician): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Technician, newItem: Technician): Boolean {
        return oldItem == newItem
    }
}
