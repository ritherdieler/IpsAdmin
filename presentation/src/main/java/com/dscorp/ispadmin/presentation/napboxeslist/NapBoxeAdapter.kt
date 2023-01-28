package com.dscorp.ispadmin.presentation.napboxeslist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemNapBoxesListBinding
import com.example.cleanarchitecture.domain.domain.entity.NapBox

class NapBoxeAdapter (val listener: OnItemClickListener): ListAdapter<NapBox, NapBoxeAdapter.NapBoxAdapterViewHolder>(NapBoxDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NapBoxAdapterViewHolder {
        val binding =
            ItemNapBoxesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NapBoxAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NapBoxAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class NapBoxAdapterViewHolder(private val binding: ItemNapBoxesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(napBox: NapBox) {
            binding.root.setOnClickListener{listener.onItemClick(napBox)}
            binding.napBoxList = napBox
            binding.executePendingBindings()
        }
    }
}



private class NapBoxDiffCallback : DiffUtil.ItemCallback<NapBox>() {
    override fun areItemsTheSame(oldItem: NapBox, newItem: NapBox): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NapBox, newItem: NapBox): Boolean {
        return oldItem == newItem
    }
}