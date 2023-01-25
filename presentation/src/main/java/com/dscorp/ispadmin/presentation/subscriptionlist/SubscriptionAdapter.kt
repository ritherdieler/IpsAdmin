package com.dscorp.ispadmin.presentation.subscriptionlist
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemSubscriptionBinding
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class SubscriptionAdapter(val listener: SubscriptionsListFragment): RecyclerView.Adapter<SubscriptionAdapter.SubscriptionListViewHolder>() {
    private var subscriptionList:List<SubscriptionResponse> = emptyList()

    fun submitList(subscription: List<SubscriptionResponse>) {
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
        fun bind(subscription: SubscriptionResponse) {
            binding.tvSeePlanId.text = subscription.plan?.id
            binding.tvSeeFirstName.text = subscription.firstName
            binding.tvSeeId.text = subscription.id
            binding.tvSeePlace.text = subscription.lastName

        }
    }

    override fun onBindViewHolder(holder: SubscriptionListViewHolder, position: Int) {
        holder.bind(subscriptionList[position])
        holder.itemView.setOnClickListener {
            listener.onItemClick(subscriptionList[position])
        }
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }
}