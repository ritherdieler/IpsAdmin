package com.dscorp.ispadmin.presentation.ui.features.payment.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.PaymentHistoryItemBinding
import com.example.cleanarchitecture.domain.domain.entity.Payment

class PaymentHistoryAdapter : ListAdapter<Payment, PaymentHistoryAdapter.PaymentHistoryViewHolder>(
    PaymentHistoryDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        val binding = PaymentHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaymentHistoryViewHolder(private val binding: PaymentHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.tvId.text = payment.id.toString()
        }
    }
}

class PaymentHistoryDiffCallback : DiffUtil.ItemCallback<Payment>() {
    override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
        return oldItem == newItem
    }

}