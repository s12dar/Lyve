package com.tech.lyve.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tech.lyve.R
import com.tech.lyve.databinding.PingItemBinding
import com.tech.lyve.models.BasketTypeUser
import com.tech.lyve.utils.Constants.REQUEST_ACCEPTED
import com.tech.lyve.utils.Constants.REQUEST_DECLINED

class PingAdapter(
    private val context: Context
) : RecyclerView.Adapter<PingAdapter.PingViewHolder>() {

    var isAcceptChecked: Boolean = false
    var isDeclineChecked: Boolean = false

    inner class PingViewHolder(private val binding: PingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val requesterName = binding.tvRequesterName

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(user: BasketTypeUser) {
            requesterName.text = "${user.name} sent a request to attend to your event"

            when (user.status) {
                REQUEST_ACCEPTED -> {
                    binding.chipDecline.visibility = View.INVISIBLE
                    binding.chipAccept.isChecked = true
                    binding.chipAccept.text = context.resources.getString(R.string.chip_accepted)
                }
                REQUEST_DECLINED -> {
                    binding.chipAccept.visibility = View.INVISIBLE
                    binding.chipDecline.isChecked = true
                    binding.chipDecline.text = context.resources.getString(R.string.chip_declined)
                }
            }

            binding.chipDecline.setOnClickListener {
                if (binding.chipDecline.isChecked) {
                    isDeclineChecked = true
                    binding.chipAccept.visibility = View.INVISIBLE
                    binding.chipDecline.text = context.resources.getString(R.string.chip_declined)
                } else {
                    isDeclineChecked = false
                    binding.chipAccept.visibility = View.VISIBLE
                    binding.chipDecline.text = context.resources.getString(R.string.chip_decline)
                }

                onRejectClickListener?.let { it(user) }
            }

            binding.chipAccept.setOnClickListener {
                if (binding.chipAccept.isChecked) {
                    isAcceptChecked = true
                    binding.chipDecline.visibility = View.INVISIBLE
                    binding.chipAccept.text = context.resources.getString(R.string.chip_accepted)
                } else {
                    isAcceptChecked = false
                    binding.chipDecline.visibility = View.VISIBLE
                    binding.chipAccept.text = context.resources.getString(R.string.chip_accept)
                }

                onAcceptClickListener?.let { it(user) }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<BasketTypeUser>() {
        override fun areItemsTheSame(oldItem: BasketTypeUser, newItem: BasketTypeUser): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: BasketTypeUser, newItem: BasketTypeUser): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PingViewHolder {
        val binding = PingItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onAcceptClickListener: ((BasketTypeUser) -> Unit)? = null
    private var onRejectClickListener: ((BasketTypeUser) -> Unit)? = null

    override fun onBindViewHolder(holder: PingViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            holder.bind(user)
        }
    }

    fun setOnAcceptClickListener(listener: (BasketTypeUser) -> Unit) {
        onAcceptClickListener = listener
    }

    fun setOnRejectClickListener(listener: (BasketTypeUser) -> Unit) {
        onRejectClickListener = listener
    }
}
