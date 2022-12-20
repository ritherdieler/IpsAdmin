package com.dscorp.ispadmin.presentation.subscriptionlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.dscorp.ispadmin.repository.model.Subscription

class SubscriptionAdapter : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {
    private var subscriptions: List<Subscription> = emptyList()

    fun submitList(subscriptions:List<Subscription>){
        this.subscriptions = subscriptions
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
       return SubscriptionViewHolder(ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    class SubscriptionViewHolder(private val binding: ItemSubscriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subscription:Subscription){
            binding.tvCliente.text = subscription.firstName
            binding.tvPlan.text = subscription.planId
            binding.tvIp.text = subscription.id

        }
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
      holder.bind(subscriptions[position])
    }

    override fun getItemCount(): Int {
        return subscriptions.size
    }

}
