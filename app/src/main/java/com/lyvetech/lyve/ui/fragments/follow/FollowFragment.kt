package com.lyvetech.lyve.ui.fragments.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.FollowAdapter
import com.lyvetech.lyve.databinding.FragmentFollowingBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.FollowViewModel
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_FOLLOWER
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_FOLLOWING
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_KEY
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowFragment : Fragment(), OnClickListener {

    private val TAG = FollowFragment::class.qualifiedName
    private lateinit var binding: FragmentFollowingBinding

    private val viewModel: FollowViewModel by viewModels()

    private var mUser = User()

    private var mFollowings = mutableListOf<User>()
    private var mFollowers = mutableListOf<User>()

    private lateinit var mArgument: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        mArgument = arguments?.getString(BUNDLE_KEY).toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageTopBarNavigation()
        manageTopAppBar()
        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            mUser = user

            if (mArgument == BUNDLE_FOLLOWING) {
                // Set Top App bar
                viewModel.getFollowings(user).observe(viewLifecycleOwner) { followings ->
                    mFollowings = followings as MutableList<User>
                    binding.rvFollow.apply {
                        adapter = FollowAdapter(
                            mUser,
                            mFollowings,
                            requireContext(),
                            this@FollowFragment
                        )
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            } else if (mArgument == BUNDLE_FOLLOWER) {
                // Set Top App bar
                viewModel.getFollowers(user).observe(viewLifecycleOwner) { followers ->
                    mFollowers = followers as MutableList<User>
                    binding.rvFollow.apply {
                        adapter = FollowAdapter(
                            mUser,
                            mFollowers,
                            requireContext(),
                            this@FollowFragment
                        )
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }
    }

    private fun manageTopAppBar() {
        if (mArgument == BUNDLE_FOLLOWING) {
            (activity as OnboardingUtils?)?.showAndSetTopAppBar("Following")
        } else if (mArgument == BUNDLE_FOLLOWER) {
            (activity as OnboardingUtils?)?.showAndSetTopAppBar("Follower")
        }
    }

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.top_app_bar) as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onPostClicked(activity: Activity) {}

    override fun onPostLongClicked(activity: Activity) {}

    override fun onUserClicked(user: User) {}

    override fun onUserFollowBtnClicked(user: User, isChecked: Boolean) {
        if (isChecked) {
            mUser.followings.add(user.uid)
            user.followers.add(mUser.uid)
        } else {
            mUser.followings.remove(user.uid)
            user.followers.remove(mUser.uid)
        }

        viewModel.updateUser(mUser)
        viewModel.updateUser(user)
    }
}