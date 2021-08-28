package com.lyvetech.lyve.onboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.databinding.FragmentRegisterBinding
import com.lyvetech.lyve.datamodels.User
import java.util.*

class RegisterFragment : Fragment() {

    private var TAG = RegisterFragment::class.qualifiedName
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnCreateAccount.setOnClickListener { view ->

            val name = binding.etName.text.toString().trim()
            val familyName = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Oops, this field couldn't be empty"
                return@setOnClickListener
            }
            if (familyName.isEmpty()) {
                binding.etFamilyName.error = "Oops, this field couldn't be empty"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = "Oops, this field couldn't be empty"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Oops, this field couldn't be empty"
                return@setOnClickListener
            }
            if (phoneNumber.isEmpty()) {
                binding.etPhoneNumber.error = "Oops, this field couldn't be empty"
                return@setOnClickListener
            }

            createAccount(email, password, name, familyName, phoneNumber)
        }


        return binding.root

    }

    private fun createAccount(
        email: String,
        password: String,
        name: String,
        lastName: String,
        phoneNumber: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user: User
//                    if (OlayiApplication.mInstance.currentUser == null) {
//                        user = User()
//                        if (FirebaseAuth.getInstance().currentUser != null) {
//                            user.userId = mAuth.currentUser?.uid!!
//                        }
//
//                        val firebaseUser: FirebaseUser = mAuth.currentUser!!
//                        firebaseUser.sendEmailVerification().addOnSuccessListener { Log.d(TAG, "Verification link is successfully sent to the user")}
//
//                        user.firstName = name
//                        user.lastName = lastName
//                        user.email = email
//                        user.phoneNumber = phoneNumber
//                        user.createdAt = Timestamp(Date()).toString()
//
//                        OlayiApplication.mInstance.currentUser = user
//                    } else {
//                        user = OlayiApplication.mInstance.currentUser!!
//                    }

//                    DataManager.mInstance.createUser(user, (false) -> {
//                        if (success != null && success) {
//                            try {
//
//                            }
//                        }
//                    })
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
