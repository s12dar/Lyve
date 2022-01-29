package com.lyvetech.lyve.ui.fragments.onboarding

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
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentProfileBinding
import com.lyvetech.lyve.ui.viewmodels.ProfileViewModel
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Top App bar
        (activity as OnboardingUtils?)?.showAndSetTopAppBar("Profile")
        manageTopBarNavigation()
        subscribeUI()
    }

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.top_app_bar) as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun subscribeUI() {
        viewModel.currentUser.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvName.text = it.name
                if (it.avatar.isNotEmpty()) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(it.avatar.toUri())
                        .into(binding.ivAvatar)
                }
            }
        }
    }
}