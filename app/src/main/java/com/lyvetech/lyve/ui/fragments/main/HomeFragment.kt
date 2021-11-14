package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User
import java.util.*

class HomeFragment : Fragment() {

    private var TAG = HomeFragment::class.qualifiedName
    private lateinit var binding: FragmentHomeBinding
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    @SuppressLint("SetTextI18n")
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

        DataManager.mInstance.getCurrentUser(object: DataListener<User> {
            override fun onData(data: User?, exception: Exception?) {
                if (data != null) {
                    LyveApplication.mInstance.currentUser = data
                    mUser = LyveApplication.mInstance.currentUser

                    val header = binding.navView.getHeaderView(0)
                    var tvName = header.findViewById<TextView>(R.id.tv_name)
                    var tvBio = header.findViewById<TextView>(R.id.tv_bio)
                    var tvFollowers = header.findViewById<TextView>(R.id.tv_followers)
                    var tvFollowing  = header.findViewById<TextView>(R.id.tv_following)

                    tvName.text = data.firstName + " " + data.lastName
                    tvBio.text = "Everything will be ok"
                    tvFollowers.text = data.nrOfFollowers.toString() + " FOLLOWERS"
                    tvFollowing.text = data.nrOfFollowings.toString() + " FOLLOWINGS"
                } else {
                    Log.i(TAG, "We can't get the data")
                }
            }
        })

//        if (mUser != null) {

//        }


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