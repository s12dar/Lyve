package com.lyvetech.lyve.ui.fragments.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.HomeAdapter
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.listeners.HomeListener
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.fragments.search.SearchFragment
import com.lyvetech.lyve.utils.Constants.BUNDLE_CURRENT_USER_KEY
import com.lyvetech.lyve.utils.Constants.BUNDLE_EVENT_KEY
import com.lyvetech.lyve.utils.Constants.BUNDLE_HOST_USER_KEY
import com.lyvetech.lyve.utils.Constants.REQUEST_LOCATION_PERMISSION
import com.lyvetech.lyve.utils.LocationUtils
import com.lyvetech.lyve.utils.OnboardingUtils
import com.lyvetech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeListener, EasyPermissions.PermissionCallbacks {

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
        requestLocationPermissions()
        getUsers()
        subscribeUI(VIEW_TYPE_ONE)
        manageUserInteraction()
    }

    private fun getUsers() {
        viewModel.getUsers().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    mUsers = result.data as MutableList<User>
//                    saveCurrentLocation(mUser)
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

    private fun requestLocationPermissions() {
        if (LocationUtils.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    }

    private fun saveCurrentLocation(user: User) {
        user.bio = "hjiiiii"
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location ->
////                user.lastLocation = GeoPoint(
////                    1.2,
////                    3.4
////                )
//                Log.i("Hello Serdar", location.latitude.toString())
//                updateCurrentUser(user)
//            }
//            .addOnFailureListener {
//                Log.e(TAG, it.toString())
//            }
        viewModel.updateUser(user).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> Log.d(TAG, "user is updated")
                is Resource.Error -> Log.e(TAG, "user cannot be updated")
                else -> {}
            }
        }
    }

    private fun isUserAlreadyHasOneEvent(): Boolean {
        if (mUser.nrOfEvents >= 1) {
            return true
        }
        return false
    }

    private fun updateCurrentUser(user: User) {
        viewModel.updateUser(user).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> Log.d(TAG, "user is updated")
                is Resource.Error -> Log.e(TAG, "user cannot be updated")
                else -> {}
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        saveCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestLocationPermissions()
        }
    }
}