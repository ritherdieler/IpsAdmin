package com.dscorp.ispadmin.presentation.subscriptionlist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.dscorp.ispadmin.repository.model.Subscription

class SubscriptionAdapter(val listener: SubscriptionsListFragment): RecyclerView.Adapter<SubscriptionAdapter.SubscriptionListViewHolder>() {
    private var subscriptionList:List<Subscription> = emptyList()

    fun submitList(subscription :List<Subscription>) {
        this.subscriptionList = subscription
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionListViewHolder {
        return SubscriptionListViewHolder(
            ItemSubscriptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class SubscriptionListViewHolder(private val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subscription:Subscription) {
            binding.tvSeePlanId.text = subscription.code
            binding.tvSeeFirstName.text = subscription.address
            binding.tvSeeId.text = subscription.id
        }
    }

    override fun onBindViewHolder(holder: SubscriptionListViewHolder, position: Int) {
        holder.bind(subscriptionList[position])
        holder.itemView.setOnClickListener { listener?.onItemClick(subscriptionList[position])
        }
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }
}