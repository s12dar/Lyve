package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeInfoBinding
import com.lyvetech.lyve.datamodels.Activity

class HomeInfoFragment : Fragment() {

    private var TAG = HomeInfoFragment::class.qualifiedName
    private lateinit var binding: FragmentHomeInfoBinding
    private var eventActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeInfoBinding.inflate(inflater, container, false)
        eventActivity = LyveApplication.mInstance.activity

        // Glide takes care of setting fetched image uri to holder
        if (eventActivity != null) {
            binding.tvLocation.text = eventActivity!!.acTitle
            binding.tvDateAndTime.text = eventActivity!!.acTime
            binding.tvParticipants.text = eventActivity!!.acParticipants.toString()

            if (eventActivity!!.acImgRefs.isNotEmpty()) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(eventActivity!!.acImgRefs.toUri())
                    .into(binding.ivAc)
            } else {
                Glide.with(requireActivity())
                    .load(requireActivity().getDrawable(R.drawable.lyve))
                    .into(binding.ivAc)
            }
        }

        return binding.root
    }
}