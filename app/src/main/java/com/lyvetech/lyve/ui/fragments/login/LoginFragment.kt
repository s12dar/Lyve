package com.lyvetech.lyve.ui.fragments.login

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
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentLoginBinding
import com.lyvetech.lyve.utils.OnboardingUtils
import com.lyvetech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

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
                editable === binding.etLoginEmail.editableText -> {
                    val email = binding.etLoginEmail.text.toString().trim()
                    if (email.isBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        binding.tilLoginEmail.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilLoginEmail.error = null
                    }
                }
                editable === binding.etLoginPassword.editableText -> {
                    val password = binding.etLoginPassword.text.toString().trim()
                    if (password.isBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        binding.tilLoginPassword.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilLoginPassword.error = null
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            etLoginEmail.addTextChangedListener(watcher)
            etLoginPassword.addTextChangedListener(watcher)

            btnSignIn.setOnClickListener {
                val email = etLoginEmail.text.toString().trim()
                val password = etLoginPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    tilLoginEmail.error =
                        getString(R.string.err_empty_field)
                    return@setOnClickListener
                }
                loginUser(email, password)
            }

            tvJoinUs.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun loginUser(
        email: String,
        pass: String
    ) {
        with(viewModel) {
            loginUser(email, pass).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        (activity as OnboardingUtils).showProgressBar()
                    }
                    is Resource.Success -> {
                        (activity as OnboardingUtils).hideProgressBar()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    is Resource.Error -> {
                        (activity as OnboardingUtils).showProgressBar()
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