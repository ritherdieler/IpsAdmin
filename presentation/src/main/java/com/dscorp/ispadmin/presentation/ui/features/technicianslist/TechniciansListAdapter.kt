package com.dscorp.ispadmin.presentation.ui.features.technicianslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemTechnicianListBinding
import com.example.cleanarchitecture.domain.domain.entity.Technician

class TechniciansListAdapter(techniciansListFragment: TechniciansListFragment) : ListAdapter<Technician, TechniciansListAdapter.TechnicianAdapterViewHolder>(
    TechnicianDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicianAdapterViewHolder {
        val binding =
            ItemTechnicianListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TechnicianAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TechnicianAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class TechnicianAdapterViewHolder(private val binding: ItemTechnicianListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(technician: Technician) {
            binding.technicianList = technician
            binding.executePendingBindings()
        }
    }
}



private class TechnicianDiffCallback : DiffUtil.ItemCallback<Technician>() {
    override fun areItemsTheSame(oldItem: Technician, newItem: Technician): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Technician, newItem: Technician): Boolean {
        return oldItem == newItem
    }
}