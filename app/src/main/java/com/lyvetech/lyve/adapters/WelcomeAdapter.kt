package com.lyvetech.lyve.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lyvetech.lyve.databinding.WelcomeItemBinding
import com.lyvetech.lyve.models.WelcomeItem

class WelcomeAdapter(
    private val context: Context,
    private val welcomeItemList: MutableList<WelcomeItem>
) :
    RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder>() {
    private lateinit var binding: WelcomeItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewHolder {
        val inflater = LayoutInflater.from(context)
        binding = WelcomeItemBinding.inflate(inflater, parent, false)
        val view: View = binding.root
        return WelcomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        holder.setWelcomeData(welcomeItemList[position])
    }

    override fun getItemCount(): Int {
        return welcomeItemList.size
    }

    inner class WelcomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = binding.tvTitle
        var tvSubtitle: TextView = binding.tvSubtitle
        var ivReward: ImageView = binding.ivReward1

        fun setWelcomeData(rewardItem: WelcomeItem) {
            tvTitle.text = rewardItem.getTitle()
            tvSubtitle.text = rewardItem.getDescription()
            ivReward.setImageResource(rewardItem.getImage())
        }
    }
}