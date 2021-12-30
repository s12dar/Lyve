package com.lyvetech.lyve.ui.fragments.follow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyvetech.lyve.adapters.FollowAdapter
import com.lyvetech.lyve.databinding.FragmentFollowingBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowFragment : Fragment(), OnClickListener {

    private val TAG = FollowFragment::class.qualifiedName
    private lateinit var binding: FragmentFollowingBinding

    private val viewModel: FollowViewModel by viewModels()

    private var mUser = User()

    private var mFollowings = mutableListOf<User>()
    private var mFollowers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            mUser = user

            viewModel.getFollowings(user).observe(viewLifecycleOwner) { followings ->
                mFollowings = followings as MutableList<User>
                val followAdapter = FollowAdapter(
                    mUser,
                    mFollowings,
                    requireContext(),
                    this@FollowFragment
                )
                val linearLayoutManager = LinearLayoutManager(context)

                binding.rvFollow.layoutManager = linearLayoutManager
                binding.rvFollow.adapter = followAdapter
            }

            viewModel.getFollowers(user).observe(viewLifecycleOwner) { followers ->
                mFollowers = followers as MutableList<User>
            }
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