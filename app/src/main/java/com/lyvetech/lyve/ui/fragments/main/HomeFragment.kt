package com.lyvetech.lyve.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.Activity
import java.util.*

class HomeFragment : Fragment() {

    private var TAG = HomeFragment::class.qualifiedName
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Declare elements for activities recyclerview
        lateinit var linearLayoutManager: LinearLayoutManager
        lateinit var homeAdapter: HomeAdapter

        DataManager.mInstance.getActivities(object : DataListener<List<Activity?>> {
            override fun onData(data: List<Activity?>?, exception: Exception?) {
                if (data != null) {

                    LyveApplication.mInstance.allActivities = data

                    homeAdapter = HomeAdapter(LyveApplication.mInstance.allActivities)
                    linearLayoutManager = LinearLayoutManager(context)

                    binding.rvActivity.layoutManager = linearLayoutManager
                    binding.rvActivity.adapter = homeAdapter

                }
            }
        })

        binding.fabAdd.setOnClickListener {
            showBottomSheetDialog()
        }

        return binding.root
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_create_activity)

        bottomSheetDialog.show()
    }
}