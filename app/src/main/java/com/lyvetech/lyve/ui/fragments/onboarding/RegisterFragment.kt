package com.lyvetech.lyve.ui.fragments.onboarding

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentRegisterBinding
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Constants.Companion.KEY_EMAIL
import com.lyvetech.lyve.utils.Constants.Companion.KEY_PASSWORD
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var TAG = RegisterFragment::class.qualifiedName
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth

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
                    if (email.isBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        binding.tilRegisterEmail.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilRegisterEmail.error = null
                    }
                }
                editable === binding.etRegisterPassword.editableText -> {
                    val password = binding.etRegisterPassword.text.toString().trim()
                    if (password.isBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        binding.tilRegisterPassword.error =
                            getString(R.string.err_empty_field)
                    } else {
                        binding.tilRegisterPassword.error = null
                    }
                }
                editable === binding.etRegisterConfirmPassword.editableText -> {
                    val confirmPassword = binding.etRegisterConfirmPassword.text.toString().trim()
                    if (confirmPassword.isBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
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
        mAuth = Firebase.auth
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

        binding.etRegisterEmail.addTextChangedListener(watcher)
        binding.etRegisterPassword.addTextChangedListener(watcher)
        binding.etRegisterConfirmPassword.addTextChangedListener(watcher)

        binding.btnCreateAccount.setOnClickListener {
            manageAccountCreation()
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun manageAccountCreation() {

        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()
        val confirmPassword = binding.etRegisterConfirmPassword.text.toString().trim()
        val userName = binding.etRegisterPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilRegisterEmail.error =
                getString(R.string.err_empty_field)
            return
        }
        if (password.isEmpty()) {
            binding.tilRegisterPassword.error =
                getString(R.string.err_empty_field)
            return
        }
        if (confirmPassword.isEmpty()) {
            binding.tilRegisterConfirmPassword.error =
                getString(R.string.err_empty_field)
            return
        }
        if (!isEmailValid(email)) {
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

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                user.uid = FirebaseAuth.getInstance().currentUser!!.uid
            }

            user.name = userName
            user.email = email
            user.pass = password

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
