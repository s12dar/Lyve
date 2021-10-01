package com.lyvetech.lyve.home

import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentBottomNavBinding
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.onboarding.LoginFragment

class HomeBottomNav : Fragment() {

    private var TAG = LoginFragment::class.qualifiedName
    private lateinit var binding: FragmentBottomNavBinding
    private lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBottomNavBinding.inflate(inflater, container, false)

        manageBottomNav();
        return binding.root
    }

    private fun manageBottomNav() {

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, HomeFragment())?.commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_home -> {
                    // Respond to navigation item 1 click
                    fragment = HomeFragment()
                    true
                }
                R.id.page_search -> {
                    // Respond to navigation item 2 click
                    fragment = ExploreFragment()
                    true
                }
                R.id.page_schedule -> {
                    // Respond to navigation item 3 click
                    fragment = ScheduleFragment()
                    true
                }
                R.id.page_profile -> {
                    // Respond to navigation item 2 click
                    fragment = ProfileFragment()
                    true
                }

                else -> false
            }
        }

    }
}