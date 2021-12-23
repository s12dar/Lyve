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
import com.lyvetech.lyve.databinding.UserItemBinding
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.listeners.OnPostClickListener
import com.lyvetech.lyve.models.User

class SearchAdapter(
    private val usersList: List<User?>?, private val activityList: List<Activity?>?,
    private val globalViewType: Int, private val context: Context,
    private val onPostClickListener: OnPostClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val activityBinding = ActivityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val userBinding = UserItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        if (globalViewType == VIEW_TYPE_ONE) {
            return SearchEventsViewHolder(activityBinding)
        }
        return SearchMembersViewHolder(userBinding)
    }

    override fun getItemCount(): Int {
        if (globalViewType == VIEW_TYPE_ONE) {
            activityList?.let {
                return it.size
            }
        }

        usersList?.let {
            return it.size
        }

        return 0
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (globalViewType == VIEW_TYPE_ONE) {
            activityList?.let {
                val activity = it[position]
                activity?.let {
                    (holder as SearchEventsViewHolder).bind(activity, onPostClickListener)
                }
            }
        } else {
            usersList?.let {
                val user = it[position]
                user?.let {
                    (holder as SearchMembersViewHolder).bind(user, onPostClickListener)
                }
            }
        }
    }

    inner class SearchEventsViewHolder(private val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val activityTitle = binding.tvTitleActivity
        private val activityLocation = binding.tvLocation
        private val activityDate = binding.tvDateAndTime
        private val activityParticipants = binding.tvParticipants

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(activity: Activity, onPostClickListener: OnPostClickListener) {
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

    inner class SearchMembersViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val userName = binding.tvName
        private val userBio = binding.tvBio
        private val userPic = binding.ivPpic

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(user: User, onPostClickListener: OnPostClickListener) {
            userName.text = user.name
            userBio.text = user.bio
            binding.root.setOnClickListener {
                onPostClickListener.onPostClicked(user)
            }
        }
    }
}