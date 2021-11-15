package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class HomeFragment : Fragment() {

    private var TAG = HomeFragment::class.qualifiedName
    private lateinit var binding: FragmentHomeBinding
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    val tvName = header.findViewById<TextView>(R.id.tv_name)
                    val tvBio = header.findViewById<TextView>(R.id.tv_bio)
                    val tvFollowers = header.findViewById<TextView>(R.id.tv_followers)
                    val tvFollowing  = header.findViewById<TextView>(R.id.tv_following)

                    tvName.text = data.firstName + " " + data.lastName
                    tvBio.text = "Everything will be ok"
                    tvFollowers.text = data.nrOfFollowers.toString() + " FOLLOWERS"
                    tvFollowing.text = data.nrOfFollowings.toString() + " FOLLOWINGS"
                } else {
                    Log.i(TAG, "We can't get the data")
                }
            }
        })

        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_create_activity)

        val ivExit = bottomSheetDialog.findViewById<ImageView>(R.id.iv_exit)
        val etSelectDate = bottomSheetDialog.findViewById<EditText>(R.id.et_select_date)

        val endDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        startDate.set(1500, 1, 1)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        var dateDialog: MaterialDatePicker<Long>? = null

        // To automatically show the last selected date, parse it to another Calendar object
        val lastDate = Calendar.getInstance()

        binding.fabAdd.setOnClickListener {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()

            var eventDateValue: LocalDate = LocalDate.of(1970, 1, 1)
            var countYearValue = true

            etSelectDate?.setOnClickListener {
                if (dateDialog == null) {
                    // Build constraints
                    val constraints =
                        CalendarConstraints.Builder()
                            .setStart(startDate.timeInMillis)
                            .setEnd(endDate.timeInMillis)
                            .setValidator(DateValidatorPointBackward.now())
                            .build()

                    // Build the dialog itself
                    dateDialog =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText(R.string.txt_activity_date)
                            .setSelection(lastDate.timeInMillis)
                            .setCalendarConstraints(constraints)
                            .build()

                    // The user pressed ok
                    dateDialog!!.addOnPositiveButtonClickListener {
                        val selection = it
                        if (selection != null) {
                            val date = Calendar.getInstance()
                            // Use a standard timezone to avoid wrong date on different time zones
                            date.timeZone = TimeZone.getTimeZone("UTC")
                            date.timeInMillis = selection
                            val year = date.get(Calendar.YEAR)
                            val month = date.get(Calendar.MONTH) + 1
                            val day = date.get(Calendar.DAY_OF_MONTH)
                            eventDateValue = LocalDate.of(year, month, day)
                            etSelectDate.setText(eventDateValue.format(formatter))
                            // The last selected date is saved if the dialog is reopened
                            lastDate.set(year, month - 1, day)
                        }

                    }
                    // Show the picker and wait to reset the variable
                    fragmentManager?.let { it1 -> dateDialog!!.show(it1, "main_act_picker") }
                    Handler(Looper.getMainLooper()).postDelayed({ dateDialog = null }, 750)
                }
            }
        }

        ivExit?.setOnClickListener {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        return binding.root
    }
}