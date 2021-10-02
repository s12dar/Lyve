package com.lyvetech.lyve.onboarding

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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentRegisterBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager.Companion.mInstance
import com.lyvetech.lyve.datamodels.User
import java.lang.Exception
import java.util.*

class RegisterFragment : Fragment() {

    private var TAG = RegisterFragment::class.qualifiedName
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth


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

        binding.etRegisterEmail.addTextChangedListener(watcher)
        binding.etRegisterPassword.addTextChangedListener(watcher)
        binding.etRegisterConfirmPassword.addTextChangedListener(watcher)

        binding.btnCreateAccount.setOnClickListener {

            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val confirmPassword = binding.etRegisterConfirmPassword.text.toString().trim()
            val firstName = binding.etRegisterPassword.text.toString().trim()
            val lastName = binding.etRegisterPassword.text.toString().trim()
            val phoneNumber = binding.etRegisterPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilRegisterEmail.error =
                    getString(R.string.err_empty_field)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilRegisterPassword.error =
                    getString(R.string.err_empty_field)
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.tilRegisterConfirmPassword.error =
                    getString(R.string.err_empty_field)
                return@setOnClickListener
            }

            if (password == confirmPassword) {
                createAccount(email, password, firstName, lastName, phoneNumber)
            } else {
                binding.tilRegisterConfirmPassword.error =
                    getString(R.string.err_password_match)
            }
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        return binding.root

    }

    private fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
    ) {
        binding.progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    var user = User()
                    if (LyveApplication.mInstance.currentUser == null) {


                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        if (firebaseUser != null) {
                            user.userId = FirebaseAuth.getInstance().currentUser!!.uid
                        }

                        user.firstName = firstName
                        user.lastName = lastName
                        user.email = email
                        user.phoneNumber = phoneNumber
                        user.createdAt = Timestamp(Date()).toString()

                        LyveApplication.mInstance.currentUser = user
                    } else {
                        user = LyveApplication.mInstance.currentUser!!
                    }

                    mInstance.createUser(user!!, object: DataListener<Boolean>{
                        override fun onData(data: Boolean?, exception: Exception?) {
                            if (data != null && data) {
                                LyveApplication.mInstance.currentUser = user
                            } else {
                                Log.e(TAG,"data has problems")
                            }
                        }
                    })
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
