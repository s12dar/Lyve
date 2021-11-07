package com.lyvetech.lyve.ui.fragments.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lyvetech.lyve.databinding.ActivityItemBinding
import com.lyvetech.lyve.datamodels.Activity


class HomeAdapter(private val activityList: List<Activity?>?) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ActivityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = activityList!!.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        with(holder) {
            with(activityList!![position]) {
                binding.tvTitleActivity.text = this!!.acTitle
                binding.tvLocation.text = this.acLocation
                binding.tvDateAndTime.text = this.acDateAndTime.toDate().date.toString()
                binding.tvParticipants.text = this.acParticipants.toString()
            }
        }
    }

    inner class HomeViewHolder(val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}