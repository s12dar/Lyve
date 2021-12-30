package com.lyvetech.lyve.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentFollowingBinding
import com.lyvetech.lyve.databinding.UserItemBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.User

class FollowAdapter(
    private val currentUser: User, private val usersList: List<User>,
    private val context: Context, private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UserItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = usersList[position]
        (holder as FollowViewHolder).bind(user, onClickListener)
    }

    override fun getItemCount(): Int {
        usersList.let {
            return it.size
        }
    }

    inner class FollowViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val userName = binding.tvName
        private val userPic = binding.ivPpic

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