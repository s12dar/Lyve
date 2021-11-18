package com.lyvetech.lyve.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lyvetech.lyve.databinding.ActivityMainBinding
import com.lyvetech.lyve.utils.OnboardingUtils

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
}