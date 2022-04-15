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
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.User

class SearchAdapter(
    private val currentUser: User, private val usersList: List<User>,
    private val eventList: List<Event>, private val globalViewType: Int,
    private val context: Context, private val onClickListener: OnClickListener
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
            eventList.let {
                return it.size
            }
        }

        usersList.let {
            return it.size
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (globalViewType == VIEW_TYPE_ONE) {
            val activity = eventList[position]
            (holder as SearchEventsViewHolder).bind(activity, onClickListener)
        } else {
            val user = usersList[position]
            (holder as SearchMembersViewHolder).bind(user, onClickListener)
        }
    }

    inner class SearchEventsViewHolder(private val binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val activityTitle = binding.tvTitleActivity
        private val activityLocation = binding.tvLocation
        private val activityDate = binding.tvDateAndTime
        private val activityParticipants = binding.tvParticipants

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(event: Event, onPostClickListener: OnClickListener) {
            activityTitle.text = event.title
            activityLocation.text = event.location.keys.first()
            activityDate.text = event.time
            activityParticipants.text = event.participants.size.toString()

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
                onPostClickListener.onPostClicked(event)
            }
        }
    }

    inner class SearchMembersViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val userName = binding.tvName
        private val userPic = binding.ivPpic

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(user: User, onUserClickListener: OnClickListener) {
            if (currentUser.followings.contains(user.uid)) {
                binding.chipBtnFollow.isChecked = true
                binding.chipBtnFollow.text = context.resources.getString(R.string.btn_following)
            }

            userName.text = user.name
            binding.root.setOnClickListener {
                onUserClickListener.onUserClicked(user)
            }

            binding.chipBtnFollow.setOnClickListener {
                val isChecked = binding.chipBtnFollow.isChecked
                if (isChecked) {
                    binding.chipBtnFollow.text = context.resources.getString(R.string.btn_following)
                } else {
                    binding.chipBtnFollow.text = context.resources.getString(R.string.btn_follow)
                }
                onUserClickListener.onUserFollowBtnClicked(user, isChecked)
            }
        }
    }
}