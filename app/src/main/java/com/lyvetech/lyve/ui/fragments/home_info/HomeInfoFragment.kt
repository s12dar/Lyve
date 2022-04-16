package com.lyvetech.lyve.ui.fragments.home_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.AttendeeAdapter
import com.lyvetech.lyve.databinding.FragmentHomeInfoBinding
import com.lyvetech.lyve.listeners.HomeInfoListener
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInfoFragment : Fragment(), HomeInfoListener {

    private val viewModel: HomeInfoViewModel by viewModels()
    private lateinit var binding: FragmentHomeInfoBinding
    private var mEvent = Event()
    private var mUser: User = User()
    private var mUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationLevelData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeInfoBinding.inflate(inflater, container, false)
        (activity as OnboardingUtils).showTopAppBar("Event")
        (activity as OnboardingUtils).hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageTopBarNavigation()
        subscribeUI()
        binding.btnAttend.setOnClickListener { manageEventAttending() }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun subscribeUI() {
        mEvent.let {
            binding.tvTitle.text = it.title
            binding.tvDateTime.text = "${it.date}, ${it.time} (GMT+3)"
            binding.tvAboutContent.text = it.desc
            for (user in mUsers) {
                if (user.uid == it.createdByID) {
                    binding.tvHostName.text = user.name
                }
            }

            val attendees = mutableListOf<User>()
            for (user in mUsers) {
                if (user.uid in it.participants) {
                    attendees.add(user)
                }
            }

            if (attendees.isNotEmpty()) {
                binding.tvAttendees.visibility = View.VISIBLE
                binding.rvAttendees.apply {
                    adapter = AttendeeAdapter(
                        attendees,
                        requireContext(),
                        this@HomeInfoFragment
                    )

                    layoutManager = LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false)
                }
            }

            if (it.imgRefs.isNotEmpty()) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(it.imgRefs.toUri())
                    .into(binding.ivAc)
            } else {
                Glide.with(requireActivity())
                    .load(requireActivity().getDrawable(R.drawable.lyve))
                    .into(binding.ivAc)
            }
        }
    }

    private fun isUserAlreadyAttending() =
        mEvent.participants.contains(mUser.uid)

    private fun manageEventAttending() {
        if (!isUserAlreadyAttending()) {
            mEvent.participants.add(mUser.uid)
            viewModel.updateActivity(mEvent, mUser)
//            showAlertMessage(true)
        } else {
//            showAlertMessage(false)
        }
    }

//    private fun showAlertMessage(isOK: Boolean) {
//        if (isOK) {
//            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
//                .setTitle(resources.getString(R.string.txt_alert_title_yay))
//                .setMessage(resources.getString(R.string.txt_alert_desc_yay))
//                .setPositiveButton(resources.getString(R.string.btn_alert_positive)) { _, _ -> }
//                .show()
//        } else {
//            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
//                .setTitle(resources.getString(R.string.txt_alert_title_oops))
//                .setMessage(resources.getString(R.string.txt_alert_desc_oops))
//                .setPositiveButton(resources.getString(R.string.btn_alert_positive)) { _, _ -> }
//                .show()
//        }
//    }

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.toolbar)
                as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getApplicationLevelData() {
        LyveApplication.mInstance.apply {
            currentUser?.let {
                mUser = it
            }
            event?.let {
                mEvent = it
            }
            allUsers.let {
                mUsers = it
            }
        }
    }

    override fun onUserClicked(user: User) {

    }
}