import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.ItemDebtorsBinding
import com.dscorp.ispadmin.presentation.ui.features.subscription.debtors.DebtorsFragment
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class DebtorsAdapter(debtorsFragment: DebtorsFragment) : ListAdapter<SubscriptionResponse, DebtorsAdapter.DebtorsAdapterViewHolder>(
    SubscriptionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtorsAdapterViewHolder {
        val binding =
            ItemDebtorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtorsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtorsAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class DebtorsAdapterViewHolder(private val binding: ItemDebtorsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subscription: SubscriptionResponse) {
            binding.subscription = subscription
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