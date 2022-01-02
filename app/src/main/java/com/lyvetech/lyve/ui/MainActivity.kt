package com.lyvetech.lyve.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lyvetech.lyve.databinding.ActivityMainBinding
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnboardingUtils {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showProgressBar() {
        binding.pb.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.pb.visibility = View.GONE
    }

    override fun showAndSetTopAppBar(toolbarTitle: String) {
        binding.topAppBar.visibility = View.VISIBLE
        binding.topAppBar.title = toolbarTitle
    }

    override fun hideTopAppBar() {
        binding.topAppBar.visibility = View.GONE
    }
}