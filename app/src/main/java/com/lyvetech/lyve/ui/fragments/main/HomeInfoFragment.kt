package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentHomeInfoBinding
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.HomeInfoViewModel
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInfoFragment : Fragment() {

    private val TAG = HomeInfoFragment::class.qualifiedName
    private val viewModel: HomeInfoViewModel by viewModels()
    private lateinit var binding: FragmentHomeInfoBinding
    private var mActivity = Activity()
    private var mUser: User = User()
    private var mUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LyveApplication.mInstance.currentUser?.let {
            mUser = it
        }
        LyveApplication.mInstance.activity?.let {
            mActivity = it
        }
        LyveApplication.mInstance.allUsers.let {
            mUsers = it
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

        // Set Top App bar
        (activity as OnboardingUtils?)?.showAndSetTopAppBar("Event")
        manageTopBarNavigation()
        subscribeUI()

        binding.btnAttend.setOnClickListener { manageEventAttending() }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun subscribeUI() {
        mActivity.let {
            binding.tvTitle.text = it.acTitle
            binding.tvDate.text = it.acTime
            binding.tvAboutContent.text = it.acDesc
            for (user in mUsers) {
                if (user.uid == it.acCreatedByID) {
                    binding.tvHostName.text = user.name
                }
            }

            val attendees = mutableListOf<User>()
            for (user in mUsers) {
                if (user.uid in it.acParticipants) {
                    attendees.add(user)
                }
            }
            when (attendees.size) {
                0 -> {
                    binding.tvAttendees.visibility = View.GONE
                    binding.viewLine3.visibility = View.GONE
                }
                1 -> {
                    binding.ivAttendee1.visibility = View.VISIBLE
                    binding.tvAttendeeName1.visibility = View.VISIBLE
                    binding.tvAttendeeName1.text = attendees[0].name
                }
                2 -> {
                    binding.ivAttendee1.visibility = View.VISIBLE
                    binding.ivAttendee2.visibility = View.VISIBLE
                    binding.tvAttendeeName1.visibility = View.VISIBLE
                    binding.tvAttendeeName2.visibility = View.VISIBLE
                    binding.tvAttendeeName1.text = attendees[0].name
                    binding.tvAttendeeName2.text = attendees[1].name
                }
                3 -> {
                    binding.ivAttendee1.visibility = View.VISIBLE
                    binding.ivAttendee2.visibility = View.VISIBLE
                    binding.ivAttendee3.visibility = View.VISIBLE
                    binding.tvAttendeeName1.visibility = View.VISIBLE
                    binding.tvAttendeeName2.visibility = View.VISIBLE
                    binding.tvAttendeeName3.visibility = View.VISIBLE
                    binding.tvAttendeeName1.text = attendees[0].name
                    binding.tvAttendeeName2.text = attendees[1].name
                    binding.tvAttendeeName3.text = attendees[2].name
                }
            }

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

    private fun isUserAlreadyAttending() = mActivity.acParticipants.contains(mUser.uid)

    private fun manageEventAttending() {
        if (!isUserAlreadyAttending()) {
            mActivity.acParticipants.add(mUser.uid)
            viewModel.updateActivity(mActivity, mUser)
            showAlertMessage(true)
        } else {
            showAlertMessage(false)
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

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.top_app_bar) as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}