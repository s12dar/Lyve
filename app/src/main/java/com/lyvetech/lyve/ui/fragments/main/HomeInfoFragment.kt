package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lyvetech.lyve.R
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeInfoBinding
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeInfoFragment : Fragment() {

    private val TAG = HomeInfoFragment::class.qualifiedName
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeInfoBinding
    private var mEventActivity = Activity()
    private var mUser: User = User()

    @Inject
    @Named("String")
    lateinit var testString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LyveApplication.mInstance.currentUser?.let {
            mUser = it
        }
        LyveApplication.mInstance.activity?.let {
            mEventActivity = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageHomeInfoUI()
        binding.btnAttend.setOnClickListener { manageEventAttending() }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun manageHomeInfoUI() {
        mEventActivity.let {
            binding.tvTitle.text = it.acTitle
            binding.tvDesc.text = it.acDesc
            binding.tvDateAndTime.text = it.acTime
            binding.tvLocation.text = it.acLocation
            binding.tvParticipants.text = it.acParticipants.size.toString()

            if (it.acImgRefs.isNotEmpty()) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(it.acImgRefs.toUri())
                    .into(binding.ivAc)
            } else {
                Glide.with(requireActivity())
                    .load(requireActivity().getDrawable(R.drawable.lyve))
                    .into(binding.ivAc)
            }
        }
    }

    private fun manageEventAttending() {
        mEventActivity.let {
            if (!it.acParticipants.contains(mUser.uid)) {
                showAlertMessage(true)

                it.acParticipants.add(mUser.uid)
                viewModel.updateActivity(it, mUser)
                LyveApplication.mInstance.activity = it
            } else {
                showAlertMessage(false)
            }
        }
    }

    private fun showAlertMessage(isOK: Boolean) {
        if (isOK) {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(resources.getString(R.string.txt_alert_title_yay))
                .setMessage(resources.getString(R.string.txt_alert_desc_yay))
                .setPositiveButton(resources.getString(R.string.btn_alert_positive)) { _, _ -> }
                .show()
        } else {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(resources.getString(R.string.txt_alert_title_oops))
                .setMessage(resources.getString(R.string.txt_alert_desc_oops))
                .setPositiveButton(resources.getString(R.string.btn_alert_positive)) { _, _ -> }
                .show()
        }
    }
}