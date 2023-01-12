package com.dscorp.ispadmin.presentation.napboxeslist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemNapBoxesListBinding
import com.dscorp.ispadmin.repository.model.NapBox

class NapBoxesAdapter (val listener: OnItemClickListener): RecyclerView.Adapter<NapBoxesAdapter.NapBoxesListViewHolder>() {
    private var napBoxesList: List<NapBox> = emptyList()

    fun submitList(napBoxes: List<NapBox>) {
        this.napBoxesList = napBoxes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NapBoxesListViewHolder {
        return NapBoxesListViewHolder(
            ItemNapBoxesListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class NapBoxesListViewHolder(private val binding: ItemNapBoxesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(napBoxes: NapBox) {
            binding.tvSeeCode.text = napBoxes.code
            binding.tvSeeAddress.text = napBoxes.address
            binding.tvSeeNapBoxId.text = napBoxes.id
        }
    }

    override fun onBindViewHolder(holder: NapBoxesListViewHolder, position: Int) {
        holder.bind(napBoxesList[position])
        holder.itemView.setOnClickListener { listener?.onItemClick(napBoxesList[position])
        }
    }

    override fun getItemCount(): Int {
        return napBoxesList.size
    }
}