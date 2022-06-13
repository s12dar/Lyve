package com.tech.lyve.ui.fragments.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.lyve.R
import com.tech.lyve.adapters.HomeAdapter
import com.tech.lyve.databinding.FragmentHomeBinding
import com.tech.lyve.listeners.HomeListener
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.ui.fragments.search.SearchFragment
import com.tech.lyve.utils.Constants.BUNDLE_CURRENT_USER_KEY
import com.tech.lyve.utils.Constants.BUNDLE_EVENT_KEY
import com.tech.lyve.utils.Constants.BUNDLE_HOST_USER_KEY
import com.tech.lyve.utils.OnboardingUtils
import com.tech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeListener {

    private val TAG = SearchFragment::class.qualifiedName
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var mUser = User()
    private var mUsers = mutableListOf<User>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var bundle: Bundle

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as OnboardingUtils).hideTopAppBar()
        (activity as OnboardingUtils).showBottomNav()
        getCurrentUser()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUsers()
        subscribeUI(VIEW_TYPE_ONE)
        manageUserInteraction()
    }

    private fun getUsers() {
        viewModel.getUsers().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    mUsers = result.data as MutableList<User>
                }
                else -> {}
            }
        }
    }

    private fun manageUserInteraction() {
        with(binding) {
            root.setOnRefreshListener {
                if (chipFollowing.isChecked) {
                    subscribeUI(VIEW_TYPE_ONE)
                } else {
                    subscribeUI(VIEW_TYPE_TWO)
                }
                root.isRefreshing = false
            }

            chipGroup.setOnCheckedChangeListener { _, _ ->
                if (chipFollowing.isChecked) {
                    subscribeUI(VIEW_TYPE_ONE)
                } else {
                    subscribeUI(VIEW_TYPE_TWO)
                }
            }

            fabAdd.setOnClickListener {
                if (isUserAlreadyHasOneEvent()) {
                    showAlertAboutLimitedEvent()
                } else {
                    findNavController().navigate(R.id.action_homeFragment_to_createEventFragment)
                }
            }
        }
    }

    private fun showAlertAboutLimitedEvent() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.alert_title))
            .setMessage(resources.getString(R.string.alert_subtitle))
            .setPositiveButton(resources.getString(R.string.alert_pos_btn)) { _, _ -> }
            .show()
    }

    private fun subscribeUI(viewType: Int) {
        when (viewType) {
            VIEW_TYPE_ONE -> {
                viewModel.getActivities().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            (activity as OnboardingUtils).showProgressBar()
                        }
                        is Resource.Success -> {
                            (activity as OnboardingUtils).hideProgressBar()
                            result.data?.let {
                                binding.rvActivity.apply {
                                    adapter = HomeAdapter(
                                        result.data,
                                        requireContext(),
                                        this@HomeFragment
                                    )
                                    layoutManager = LinearLayoutManager(context)
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
                    }
                }
            }

            VIEW_TYPE_TWO -> {
                viewModel.getFollowingActivities(mUser).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            (activity as OnboardingUtils).showProgressBar()
                        }
                        is Resource.Success -> {
                            (activity as OnboardingUtils).hideProgressBar()
                            result.data?.let {
                                binding.rvActivity.apply {
                                    adapter = HomeAdapter(
                                        result.data,
                                        requireContext(),
                                        this@HomeFragment
                                    )
                                    layoutManager = LinearLayoutManager(context)
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
                    }
                }
            }
        }
    }

    private fun getCurrentUser() {
        with(viewModel) {
            getCurrentUser().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            mUser = result.data
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getHostOfActivity(event: Event): User {
        var hostUser = User()
        for (user in mUsers) {
            if (user.uid == event.createdByID) {
                hostUser = user
            }
        }
        return hostUser
    }

    override fun onPostClicked(event: Event) {
        bundle.apply {
            putSerializable(BUNDLE_EVENT_KEY, event)
            putSerializable(BUNDLE_HOST_USER_KEY, getHostOfActivity(event))
            putSerializable(BUNDLE_CURRENT_USER_KEY, mUser)
        }
        findNavController().navigate(R.id.action_homeFragment_to_homeInfoFragment, bundle)
    }

    private fun isUserAlreadyHasOneEvent() = mUser.nrOfEvents >= 1
}