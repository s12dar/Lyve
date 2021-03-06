package com.tech.lyve.ui.fragments.home_info

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.google.android.material.snackbar.Snackbar
import com.tech.lyve.R
import com.tech.lyve.databinding.FragmentHomeInfoBinding
import com.tech.lyve.listeners.HomeInfoListener
import com.tech.lyve.models.BasketTypeEvent
import com.tech.lyve.models.BasketTypeUser
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.utils.Constants.BUNDLE_CURRENT_USER_KEY
import com.tech.lyve.utils.Constants.BUNDLE_EVENT_KEY
import com.tech.lyve.utils.Constants.BUNDLE_HOST_USER_KEY
import com.tech.lyve.utils.Constants.INTENT_GOOGLE_MAPS
import com.tech.lyve.utils.OnboardingUtils
import com.tech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInfoFragment : Fragment(), HomeInfoListener {

    private val viewModel: HomeInfoViewModel by viewModels()
    private lateinit var binding: FragmentHomeInfoBinding
    private var mEvent = Event()
    private var mHostUser = User()
    private var mCurrentUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeInfoBinding.inflate(inflater, container, false)
        manageBottomAndTopBar()
        managePassedArguments(arguments)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageTopBarNavigation()
        manageBindingViews()
        subscribeUI()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun subscribeUI() {
        mEvent.let {
            binding.tvTitle.text = it.title
            binding.tvDateTime.text = "${it.date}, ${it.time} (GMT+3)"
            binding.tvAboutContent.text = it.desc
            binding.tvHostName.text = mHostUser.name
            if (!it.isOnline) {
                binding.ivIconOnline.visibility = View.INVISIBLE
                binding.tvOnline.visibility = View.INVISIBLE
                binding.ivIconLocation.visibility = View.VISIBLE
                binding.tvLocation.visibility = View.VISIBLE
                binding.tvLocation.text = it.location.keys.first()
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

    private fun isUserAlreadyAttending(): Boolean {
        for (participant in mEvent.participants) {
            if (participant.uid == mCurrentUser.uid) {
                return true
            }
        }
        return false
    }

    private fun manageEventAttending() {
        (activity as OnboardingUtils).showProgressBar()
        if (!isUserAlreadyAttending()) {
            mEvent.participants.add(
                BasketTypeUser(
                    uid = mCurrentUser.uid,
                    name = mCurrentUser.name,
                    status = "pending"
                )
            )
            mCurrentUser.attendings.add(
                BasketTypeEvent(
                    uid = mEvent.uid,
                    imgUrl = mEvent.imgRefs,
                    title = mEvent.title,
                    desc = mEvent.desc,
                    createdBy = mEvent.createdByID,
                    date = mEvent.date
                )
            )
            viewModel.apply {
                updateEvent(mEvent, mHostUser)
                    .observe(viewLifecycleOwner) { eventResult ->
                        when (eventResult) {
                            is Resource.Success -> {
                                viewModel.updateUser(mCurrentUser)
                                    .observe(viewLifecycleOwner) { result ->
                                        when (result) {
                                            is Resource.Success -> {
                                                (activity as OnboardingUtils).hideProgressBar()
                                                Snackbar.make(
                                                    requireView(),
                                                    "Yaay, we sent your request!",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                            }
                                            else -> {}
                                        }
                                    }
                            }
                            is Resource.Error -> {
                                (activity as OnboardingUtils).hideProgressBar()
                                Snackbar.make(
                                    requireView(),
                                    "Oops, something went wrong!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
            }
        } else {
            (activity as OnboardingUtils).hideProgressBar()
            Snackbar.make(
                requireView(),
                "Oops, you've already requested attendance!",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.toolbar)
                as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun launchGoogleMaps() {
        val lng = mEvent.location.values.first().longitude
        val lat = mEvent.location.values.first().latitude

        val googleMapsIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsIntentUri)

        googleMapsIntent.setPackage(INTENT_GOOGLE_MAPS)
        googleMapsIntent.resolveActivity(requireActivity().packageManager)?.let {
            startActivity(googleMapsIntent)
        }
    }

    private fun launchBrowser() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://${mEvent.url}"))
        startActivity(browserIntent)
    }

    private fun manageBindingViews() {
        with(binding) {
            tvLocation.setOnClickListener { launchGoogleMaps() }
            tvOnline.setOnClickListener { launchBrowser() }
            btnAttend.setOnClickListener { manageEventAttending() }
        }
    }

    private fun manageBottomAndTopBar() {
        (activity as OnboardingUtils).showTopAppBar("Event")
        (activity as OnboardingUtils).hideBottomNav()
    }

    private fun managePassedArguments(argument: Bundle?) {
        argument?.let {
            mEvent = it.getSerializable(BUNDLE_EVENT_KEY) as Event
            mHostUser = it.getSerializable(BUNDLE_HOST_USER_KEY) as User
            mCurrentUser = it.getSerializable(BUNDLE_CURRENT_USER_KEY) as User
        }
    }

    override fun onUserClicked(user: User) {}
}