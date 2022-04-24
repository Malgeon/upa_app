package com.example.upa_app.presentation.sessioncommon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.presentation.databinding.ItemSessionBinding
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.ZoneId


class SessionsAdapter(
    private val tagViewPool: RecyclerView.RecycledViewPool,
    private val showReservations: StateFlow<Boolean>,
    private val timeZoneId: StateFlow<ZoneId>,
    private val lifecycleOwner: LifecycleOwner,
    private val onSessionClickListener: OnSessionClickListener,
    private val onSessionStartClickListener: OnSessionStarClickListener
) : ListAdapter<UserSession, SessionViewHolder>(SessionDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).apply {
            tags.apply {
                setRecycledViewPool(tagViewPool)
                layoutManager = FlexboxLayoutManager(parent.context).apply {
                    recycleChildrenOnDetach = true
                }
            }
            showReservations = this@SessionsAdapter.showReservations
            timeZoneId = this@SessionsAdapter.timeZoneId
            showTime = false
            lifecycleOwner = this@SessionsAdapter.lifecycleOwner
            sessionClickListener = onSessionClickListener
            sessionStarClickListener = onSessionStartClickListener
        }
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.binding.userSession = getItem(position)
        holder.binding.executePendingBindings()
    }
}

class SessionViewHolder(
    internal val binding: ItemSessionBinding
) : RecyclerView.ViewHolder(binding.root)

object SessionDiff : DiffUtil.ItemCallback<UserSession>() {
    override fun areItemsTheSame(oldItem: UserSession, newItem: UserSession): Boolean {
        // We don't have to compare the userEvent id because it matches the session id.
        return oldItem.session.id == newItem.session.id
    }

    override fun areContentsTheSame(oldItem: UserSession, newItem: UserSession): Boolean {
        return oldItem == newItem
    }
}