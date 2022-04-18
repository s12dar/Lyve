package com.lyvetech.lyve.ui.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.WelcomeAdapter
import com.lyvetech.lyve.databinding.FragmentWelcomeBinding
import com.lyvetech.lyve.models.WelcomeItem

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var welcomeAdapter: WelcomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWelcomeAdapter()

        val welcomeViewPager = binding.vpWelcome
        welcomeViewPager.adapter = welcomeAdapter

        setUpIndicators()
        setUpCurrentIndicator(0)

        welcomeViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setUpCurrentIndicator(position)
            }
        })

        binding.apply {
            btnSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
            }
        }
    }

    private fun setUpWelcomeAdapter() {
        val firstPage = WelcomeItem()
            .apply {
                setTitle(resources.getString(R.string.txt_title_welcome))
                setDescription(resources.getString(R.string.txt_desc_welcome))
                setImage(R.drawable.ic_parachute)
            }

        val secondPage = WelcomeItem()
            .apply {
                setTitle(resources.getString(R.string.txt_title_welcome2))
                setDescription(resources.getString(R.string.txt_desc_welcome2))
                setImage(R.drawable.lyve)
            }

        val thirdPage = WelcomeItem()
            .apply {
                setTitle(resources.getString(R.string.txt_title_welcome3))
                setDescription(resources.getString(R.string.txt_desc_welcome3))
                setImage(R.drawable.lyve)
            }

        val welcomeItemList = mutableListOf<WelcomeItem>()
            .apply {
                add(firstPage)
                add(secondPage)
                add(thirdPage)
            }
        welcomeAdapter =
            WelcomeAdapter(requireContext(), welcomeItemList)
    }

    private fun setUpIndicators() {
        val indicators = arrayOfNulls<ImageView>(welcomeAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext().applicationContext)
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext().applicationContext,
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]!!.layoutParams = layoutParams
            binding.indicators.addView(indicators[i])
        }
    }

    private fun setUpCurrentIndicator(index: Int) {
        val childCount = binding.indicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext().applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext().applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}