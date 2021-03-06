package com.tech.lyve.ui.fragments.register

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tech.lyve.R
import com.tech.lyve.databinding.FragmentRegisterBinding
import com.tech.lyve.models.User
import com.tech.lyve.utils.Constants.KEY_EMAIL
import com.tech.lyve.utils.Constants.KEY_PASSWORD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    @Inject
    lateinit var auth: FirebaseAuth

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
                editable === binding.etRegisterEmail.editableText -> {
                    val email = binding.etRegisterEmail.text.toString().trim()
                    if (email.isEmpty()) {
                        binding.tilRegisterEmail.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilRegisterEmail.error = null
                    }
                }
                editable === binding.etRegisterPassword.editableText -> {
                    val password = binding.etRegisterPassword.text.toString().trim()
                    if (password.isEmpty()) {
                        binding.tilRegisterPassword.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilRegisterPassword.error = null
                    }
                }
                editable === binding.etRegisterConfirmPassword.editableText -> {
                    val confirmPassword = binding.etRegisterConfirmPassword.text.toString().trim()
                    if (confirmPassword.isBlank()) {
                        binding.tilRegisterConfirmPassword.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilRegisterConfirmPassword.error = null
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            etRegisterEmail.addTextChangedListener(watcher)
            etRegisterPassword.addTextChangedListener(watcher)
            etRegisterConfirmPassword.addTextChangedListener(watcher)

            btnCreateAccount.setOnClickListener {
                manageAccountCreation()
            }
            tvSignIn.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun manageAccountCreation() {
        val emailAddress = binding.etRegisterEmail.text.toString()
        val password = binding.etRegisterPassword.text.toString()
        val confirmPassword = binding.etRegisterConfirmPassword.text.toString()

        if (emailAddress.isBlank()) {
            binding.tilRegisterEmail.error =
                getString(R.string.err_empty_field)
            return
        }
        if (password.isBlank()) {
            binding.tilRegisterPassword.error =
                getString(R.string.err_empty_field)
            return
        }
        if (confirmPassword.isBlank()) {
            binding.tilRegisterConfirmPassword.error =
                getString(R.string.err_empty_field)
            return
        }
        if (!isEmailValid(emailAddress)) {
            binding.tilRegisterEmail.error =
                getString(R.string.err_invalid_email)
            return
        }
        if (password.length < 6) {
            binding.tilRegisterPassword.error =
                getString(R.string.err_invalid_pass)
            return
        }

        if (password == confirmPassword) {
            val user = User()
            user.apply {
                email = emailAddress
                pass = password
            }

            sharedPref.edit()
                .putString(KEY_PASSWORD, user.pass)
                .putString(KEY_EMAIL, user.email)
                .apply()

            findNavController().navigate(R.id.action_registerFragment_to_onboardingFragment)
        } else {
            binding.tilRegisterConfirmPassword.error =
                getString(R.string.err_password_match)
        }
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
}
