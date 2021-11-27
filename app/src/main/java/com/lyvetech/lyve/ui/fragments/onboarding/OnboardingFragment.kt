package com.lyvetech.lyve.ui.fragments.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentOnboardingBinding
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.User
import com.lyvetech.lyve.listeners.DataListener
import com.lyvetech.lyve.utils.OnboardingUtils
import java.util.*

class OnboardingFragment : Fragment() {

    private val TAG = OnboardingFragment::class.qualifiedName
    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: User

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
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
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
        mAuth = Firebase.auth
        mUser = LyveApplication.mInstance.currentUser!!
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        binding.etInput.addTextChangedListener(watcher)

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etInput.text.toString().trim()

            if (name.isEmpty()) {
                binding.tilInput.error =
                    getString(R.string.err_invalid_email)
                return@setOnClickListener
            } else {
                mUser.name = name
            }
            createAccount(mUser)
        }

        return binding.root
    }

    private fun createAccount(user: User) {
        (context as OnboardingUtils?)!!.showProgressBar()

        mAuth.createUserWithEmailAndPassword(user.email, user.pass)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    if (firebaseUser != null) {
                        user.uid = FirebaseAuth.getInstance().currentUser!!.uid
                    }

                    LyveApplication.mInstance.currentUser = user

                    DataManager.mInstance.createUser(user, object : DataListener<Boolean> {
                        override fun onData(data: Boolean?, exception: Exception?) {
                            if (data != null && data) {
                                LyveApplication.mInstance.currentUser = user
                            } else {
                                Log.e(TAG, "data has problems")
                            }
                        }
                    })

                    (context as OnboardingUtils?)!!.hideProgressBar()
                    findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}