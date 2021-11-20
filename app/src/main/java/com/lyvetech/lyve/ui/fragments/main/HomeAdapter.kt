package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.ActivityItemBinding
import com.lyvetech.lyve.datamodels.Activity


class HomeAdapter(private val activityList: List<Activity?>?, private val context: Context) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ActivityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = activityList!!.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        with(holder) {
            with(activityList!![position]) {
                binding.tvTitleActivity.text = this!!.acTitle
                binding.tvLocation.text = this.acLocation
                binding.tvDateAndTime.text = this.acTime
                binding.tvParticipants.text = this.acParticipants.toString()

                // Glide takes care of setting fetched image uri to holder
                if (activityList[position]!!.acImgRefs.isNotEmpty()) {
                    Glide.with(context)
                        .asBitmap()
                        .load(this.acImgRefs.toUri())
                        .into(binding.ivAc)
                } else {
                    Glide.with(context)
                        .load(context.getDrawable(R.drawable.lyve))
                        .into(binding.ivAc)
                }
            }
        }
    }

    inner class HomeViewHolder(val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}