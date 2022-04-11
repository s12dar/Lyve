package com.lyvetech.lyve.ui.fragments.register

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentOnboardingBinding
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Constants.KEY_EMAIL
import com.lyvetech.lyve.utils.Constants.KEY_PASSWORD
import com.lyvetech.lyve.utils.OnboardingUtils
import com.lyvetech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SecondRegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentOnboardingBinding
    private var mUser = User()

    @Inject
    lateinit var sharedPref: SharedPreferences

    // Validate each field in the form with the same watcher
    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun afterTextChanged(editable: Editable) {
            when {
                editable === binding.etInput.editableText -> {
                    val email = binding.etInput.text.toString().trim()
                    if (email.isBlank()) {
                        binding.tilInput.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilInput.error = null
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assignUserDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            etInput.addTextChangedListener(watcher)

            btnCreateAccount.setOnClickListener {
                val name = etInput.text.toString().trim()
                if (name.isEmpty()) {
                    tilInput.error =
                        getString(R.string.err_invalid_email)
                    return@setOnClickListener
                } else {
                    mUser.name = name
                }

                createAccount()
            }
        }
    }

    private fun createAccount() {
        with(viewModel) {
            createUser(mUser).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        (activity as OnboardingUtils).showProgressBar()
                    }
                    is Resource.Success -> {
                        (activity as OnboardingUtils).hideProgressBar()
                        findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
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

    private fun assignUserDetails() {
        mUser.apply {
            email = sharedPref.getString(KEY_EMAIL, "").toString()
            pass = sharedPref.getString(KEY_PASSWORD, "").toString()
        }
    }
}