package com.lyvetech.lyve.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.ActivityItemBinding
import com.lyvetech.lyve.listeners.HomeListener
import com.lyvetech.lyve.models.Event

class HomeAdapter(
    private val eventList: List<Event>, private val context: Context,
    private val homeListener: HomeListener
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ActivityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = eventList.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val activity = eventList[position]
        holder.bind(activity, homeListener)
    }

    inner class HomeViewHolder(private val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val title = binding.tvTitle
        private val location = binding.tvLocation
        private val dateAndTime = binding.tvDateAndTime
        private val participants = binding.tvParticipants

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(event: Event, homeListener: HomeListener) {
            title.text = event.title
            if (event.isOnline) {
                location.text = "Online event"
            } else {
                location.text = event.location.keys.first()
            }
            dateAndTime.text = "${event.date} ${event.time}"
            participants.text = event.participants.size.toString()

            // Glide takes care of setting fetched image uri to holder
            if (event.imgRefs.isNotEmpty()) {
                Glide.with(context)
                    .asBitmap()
                    .load(event.imgRefs.toUri())
                    .into(binding.ivAc)
            } else {
                Glide.with(context)
                    .load(context.getDrawable(R.drawable.lyve))
                    .into(binding.ivAc)
            }

            binding.root.setOnClickListener {
                homeListener.onPostClicked(event)
            }
        }
    }
}