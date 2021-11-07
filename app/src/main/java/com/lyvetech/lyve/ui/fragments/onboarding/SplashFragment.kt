package com.lyvetech.lyve.ui.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentSplashBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User
import java.lang.Exception
import java.util.*

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DataManager.mInstance.getCurrentUser(object : DataListener<User> {
            override fun onData(data: User?, exception: Exception?) {
                if (data != null) {
                    LyveApplication.mInstance.currentUser = data
                }
                DataManager.mInstance.getActivities(object : DataListener<List<Activity?>> {
                    override fun onData(data: List<Activity?>?, exception: Exception?) {
                        if (data != null) {
                            LyveApplication.mInstance.allActivities = data
                        } else {
                            LyveApplication.mInstance.allActivities = LinkedList()
                        }
                    }
                })
            }
        })
        goToNextScreen()
    }

    private fun goToNextScreen() {
        if (isDetached || isRemoving || activity == null) {
            return
        }

        if (mUser != null) {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }
}