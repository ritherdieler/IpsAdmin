package com.dscorp.ispadmin.presentation.ui.features.supportTicket

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.closedTickets.ClosedTicketsFragment
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.pendingTIckets.PendingTicketsFragment
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.takenTickets.TakenTicketsFragment

class TicketsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingTicketsFragment()
            1 -> TakenTicketsFragment()
            2 -> ClosedTicketsFragment()
            else -> throw IllegalArgumentException("Posición no válida")
        }
    }
}