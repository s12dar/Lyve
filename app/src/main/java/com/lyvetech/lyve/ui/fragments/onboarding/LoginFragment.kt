package com.lyvetech.lyve.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentLoginBinding
import com.lyvetech.lyve.datamodels.User

class LoginFragment : Fragment() {

    private var TAG = LoginFragment::class.qualifiedName
    private lateinit var binding: FragmentLoginBinding
    private lateinit var maAuth: FirebaseAuth

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
        maAuth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.etLoginEmail.addTextChangedListener(watcher)
        binding.etLoginPassword.addTextChangedListener(watcher)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilLoginEmail.error =
                    getString(R.string.err_empty_field)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilLoginPassword.error =
                    getString(R.string.err_empty_field)
                return@setOnClickListener
            }

            signIn(email, password)
        }

        binding.tvJoinUs.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    private fun signIn(
        email: String,
        password: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        maAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    Log.d(TAG, "signInWithEmailAndPassword:success")
                    val user: User
                } else {
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}