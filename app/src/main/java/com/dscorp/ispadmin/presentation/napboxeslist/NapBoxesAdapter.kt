package com.dscorp.ispadmin.presentation.napboxeslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemNapBoxesListBinding
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.dscorp.ispadmin.repository.model.NapBox
import com.dscorp.ispadmin.repository.model.Subscription

class NapBoxesAdapter : RecyclerView.Adapter<NapBoxesAdapter.NapBoxesListViewHolder>() {
    private var napBoxesList: List<NapBox> = emptyList()

    fun submitList(napBoxes: List<NapBox>){
        this.napBoxesList = napBoxes
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NapBoxesListViewHolder {
       return NapBoxesListViewHolder(ItemNapBoxesListBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    class NapBoxesListViewHolder(private val binding: ItemNapBoxesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(napBoxes:NapBox){
            binding.tvPrueba0NapBoxes.text = napBoxes.code
            binding.tvPrueba1NapBoxes.text = napBoxes.address
            binding.tvPrueba2NapBoxes.text = napBoxes.id
        }
    }

    override fun onBindViewHolder(holder: NapBoxesListViewHolder, position: Int) {
      holder.bind(napBoxesList[position])
    }

    override fun getItemCount(): Int {
        return napBoxesList.size
    }

}
