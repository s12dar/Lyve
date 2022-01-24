package com.lyvetech.lyve.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.AttendeeItemBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.User

class AttendeeAdapter(
    private val attendeesList: List<User>, private val context: Context,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AttendeeItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = attendeesList[position]
        (holder as AttendeeAdapter.AttendeeViewHolder).bind(user, onClickListener)
    }

    override fun getItemCount(): Int {
        attendeesList.let {
            return it.size
        }
    }

    inner class AttendeeViewHolder(private val binding: AttendeeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val attendeeName = binding.tvNameAttendee

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(attendee: User, onUserClickListener: OnClickListener) {
            attendeeName.text = attendee.name

            Glide.with(context)
                .load(context.getDrawable(R.drawable.ic_profile_15dp))
                .into(binding.ivAvatarAttendee)

            binding.root.setOnClickListener {
                onUserClickListener.onUserClicked(attendee)
            }
        }
    }

}