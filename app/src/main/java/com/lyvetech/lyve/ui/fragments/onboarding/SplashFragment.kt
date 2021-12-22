package com.lyvetech.lyve.ui.fragments.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var TAG = SplashFragment::class.qualifiedName
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

//        viewModel.currentUser.observe(viewLifecycleOwner) {
//            Log.d(TAG, "hi SERDARCHIK")
//            LyveApplication.mInstance.currentUser = it
//
//            viewModel.allActivities.observe(viewLifecycleOwner) { activities ->
//                LyveApplication.mInstance.allActivities = activities as MutableList<Activity?>?
//            }
//        }

        Handler(Looper.getMainLooper()).postDelayed({ goToNextScreen() }, 5000)
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