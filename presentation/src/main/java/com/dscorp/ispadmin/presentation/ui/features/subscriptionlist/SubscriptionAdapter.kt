package com.dscorp.ispadmin.presentation.ui.features.subscriptionlist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class SubscriptionAdapter(val listener: SubscriptionItemClickListener): ListAdapter<SubscriptionResponse, SubscriptionAdapter.SubscriptionAdapterViewHolder>(
    SubscriptionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionAdapterViewHolder {
        val binding =
            ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscriptionAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
   inner class SubscriptionAdapterViewHolder(private val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subscription: SubscriptionResponse) {
            binding.root.setOnClickListener{listener.onItemClick(subscription)}
            binding.subscriptionList = subscription
            binding.executePendingBindings()
        }
    }
}



private class SubscriptionDiffCallback : DiffUtil.ItemCallback<SubscriptionResponse>() {
    override fun areItemsTheSame(oldItem: SubscriptionResponse, newItem: SubscriptionResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SubscriptionResponse, newItem: SubscriptionResponse): Boolean {
        return oldItem == newItem
    }
}