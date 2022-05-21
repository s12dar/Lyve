package com.lyvetech.lyve.ui.fragments.ping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lyvetech.lyve.databinding.FragmentPingBinding
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.OnboardingUtils
import com.lyvetech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PingFragment : Fragment() {

    private lateinit var binding: FragmentPingBinding
    private val viewModel: PingViewModel by viewModels()
    private var mUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrentUser()
    }

    private fun getEventBelongingToUser(user: User) {
        viewModel.getEventBelongingToCurrentUser(user).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    (activity as OnboardingUtils).showProgressBar()
                }
                is Resource.Success -> {
                    (activity as OnboardingUtils).hideProgressBar()
                    result.data?.let {

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

    private fun getCurrentUser() {
        with(viewModel) {
            getCurrentUser().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            mUser = result.data
                            getEventBelongingToUser(mUser)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}