package com.dscorp.ispadmin.presentation.technicianslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemTechniciansListBinding
import com.example.cleanarchitecture.domain.domain.entity.Technician

class TechniciansListAdapter :
    RecyclerView.Adapter<TechniciansListAdapter.TechniciansListViewHolder>() {
    private var techniciansList: List<Technician> = emptyList()

    fun submitList(techniciansList: List<Technician>) {
        this.techniciansList = techniciansList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechniciansListViewHolder {
        return TechniciansListViewHolder(
            ItemTechniciansListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    class TechniciansListViewHolder(private val binding: ItemTechniciansListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(techniciansList: Technician) {
            binding.tvPrueba0Technician.text = techniciansList.name
            binding.tvPrueba1Technician.text = techniciansList.id
            binding.tvPrueba2Technician.text = techniciansList.lastName

        }
    }

    override fun onBindViewHolder(holder: TechniciansListViewHolder, position: Int) {
        holder.bind(techniciansList[position])
    }

    override fun getItemCount(): Int {
        return techniciansList.size
    }

}
