package com.lyvetech.lyve.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyvetech.lyve.databinding.FragmentExploreBinding
import com.lyvetech.lyve.onboarding.LoginFragment

class ExploreFragment : Fragment() {

    private var TAG = LoginFragment::class.qualifiedName
    private lateinit var binding: FragmentExploreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        return binding.root
    }
}