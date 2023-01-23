package com.dscorp.ispadmin.presentation.subscriptionfinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemFindSubscriptionBinding
import com.example.cleanarchitecture.domain.domain.entity.Subscription

class FindSubscriptionAdapter :ListAdapter<Subscription, FindSubscriptionViewHolder>(SubscriptionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindSubscriptionViewHolder {
        val binding =
            ItemFindSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindSubscriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindSubscriptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class FindSubscriptionViewHolder(private val binding: ItemFindSubscriptionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(subscription: Subscription) {
        binding.subscription = subscription
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