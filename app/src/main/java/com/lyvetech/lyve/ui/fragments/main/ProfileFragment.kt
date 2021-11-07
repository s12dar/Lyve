package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentProfileBinding
import com.lyvetech.lyve.datamodels.User

class ProfileFragment : Fragment() {

    private var TAG = ProfileFragment::class.qualifiedName
    private lateinit var binding: FragmentProfileBinding
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // THIS NEEDS TO BE HANDLED WHEN LOG IN ACTION IS DONE, BECAUSE FROM LOG IN TO PROFILE THERE IS NO SPLASH

//        DataManager.mInstance.getCurrentUser(object: DataListener<User> {
//            override fun onData(data: User?, exception: Exception?) {
//                if (data != null) {
//                    LyveApplication.mInstance.currentUser = data
//                } else {
//                    Log.i(TAG, "We can't get the data")
//                }
//            }
//        })

        mUser = LyveApplication.mInstance.currentUser
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        if (mUser != null) {
            binding.tvName.text = mUser!!.firstName + " " + mUser!!.lastName
            binding.tvBio.text = "Everything will be ok"
            binding.tvFollowers.text = mUser!!.nrOfFollowers.toString() + " FOLLOWERS"
            binding.tvFollowing.text = mUser!!.nrOfFollowings.toString() + " FOLLOWINGS"
        }

        return binding.root
    }
}