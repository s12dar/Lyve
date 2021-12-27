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
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.listeners.OnClickListener

class HomeAdapter(
    private val activityList: List<Activity>, private val context: Context,
    private val onPostClickListener: OnClickListener
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ActivityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = activityList.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val activity = activityList[position]
        holder.bind(activity, onPostClickListener)
    }

    inner class HomeViewHolder(private val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val activityTitle = binding.tvTitleActivity
        private val activityLocation = binding.tvLocation
        private val activityDate = binding.tvDateAndTime
        private val activityParticipants = binding.tvParticipants

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(activity: Activity, onPostClickListener: OnClickListener) {
            activityTitle.text = activity.acTitle
            activityLocation.text = activity.acLocation
            activityDate.text = activity.acTime
            activityParticipants.text = activity.acParticipants.size.toString()

            // Glide takes care of setting fetched image uri to holder
            if (activity.acImgRefs.isNotEmpty()) {
                Glide.with(context)
                    .asBitmap()
                    .load(activity.acImgRefs.toUri())
                    .into(binding.ivAc)
            } else {
                Glide.with(context)
                    .load(context.getDrawable(R.drawable.lyve))
                    .into(binding.ivAc)
            }

            binding.root.setOnClickListener {
                onPostClickListener.onPostClicked(activity)
            }
        }
    }
}