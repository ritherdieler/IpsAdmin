package com.dscorp.ispadmin.presentation.subscriptionlist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.example.cleanarchitecture.domain.domain.entity.Subscription

class SubscriptionAdapter: ListAdapter<Subscription, SubscriptionAdapterViewHolder>(SubscriptionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionAdapterViewHolder {
        val binding =
            ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscriptionAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SubscriptionAdapterViewHolder(private val binding: ItemSubscriptionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(subscription: Subscription) {
        binding.subscriptionList = subscription
        binding.executePendingBindings()
    }
}

private class SubscriptionDiffCallback : DiffUtil.ItemCallback<Subscription>() {
    override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
        return oldItem == newItem
    }
}