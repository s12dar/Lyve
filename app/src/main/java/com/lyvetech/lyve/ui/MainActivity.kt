package com.lyvetech.lyve.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.ActivityMainBinding
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnboardingUtils {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        manageBottomNavigation()
        setContentView(binding.root)
    }

    override fun showProgressBar() {
        binding.pb.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.pb.visibility = View.GONE
    }

    override fun showTopAppBar(title: String) {
        binding.toolbar.title = title
        binding.appBarLayout.visibility = View.VISIBLE
    }

    override fun hideTopAppBar() {
        binding.appBarLayout.visibility = View.GONE
    }

    override fun showBottomNav() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun hideBottomNav() {
        binding.bottomNavigation.visibility = View.GONE
    }

    private fun manageBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.findNavController())
    }
}