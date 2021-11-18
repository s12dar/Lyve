package com.lyvetech.lyve.ui.fragments.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lyvetech.lyve.R
import com.lyvetech.lyve.application.LyveApplication
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_ACTIVITIES
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class HomeFragment : Fragment() {

    private var TAG = HomeFragment::class.qualifiedName
    private lateinit var binding: FragmentHomeBinding
    private var mUser: User? = null
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private var imageChosen = false

    private lateinit var mIvExit: ImageView
    private lateinit var mIvActivityAvatar: ImageView
    private lateinit var mEtSelectedDate: EditText
    private lateinit var mTilSelectedDate: TextInputLayout
    private lateinit var mEtActivityName: EditText
    private lateinit var mTilActivityName: TextInputLayout
    private lateinit var mEtActivityLocation: EditText
    private lateinit var mTilActivityLocation: TextInputLayout
    private lateinit var mEtActivityDesc: EditText
    private lateinit var mTilActivityDesc: TextInputLayout
    private lateinit var mBtnCreateActivity: Button
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mProgressBar: ProgressBar
    private var urlForDocument: Uri? = null
    private var urlLocal: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        // Bottom sheet layout components
        bottomSheetDialog = context?.let { BottomSheetDialog(it) }!!
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_create_activity)

        mIvActivityAvatar = bottomSheetDialog.findViewById(R.id.iv_activity)!!
        mIvExit = bottomSheetDialog.findViewById(R.id.iv_exit)!!
        mEtSelectedDate = bottomSheetDialog.findViewById(R.id.et_select_date)!!
        mTilSelectedDate = bottomSheetDialog.findViewById(R.id.til_select_date)!!
        mEtActivityName = bottomSheetDialog.findViewById(R.id.et_activity_name)!!
        mTilActivityName = bottomSheetDialog.findViewById(R.id.til_activity_name)!!
        mEtActivityLocation = bottomSheetDialog.findViewById(R.id.et_activity_location)!!
        mTilActivityLocation = bottomSheetDialog.findViewById(R.id.til_activity_location)!!
        mEtActivityDesc = bottomSheetDialog.findViewById(R.id.et_activity_desc)!!
        mTilActivityDesc = bottomSheetDialog.findViewById(R.id.til_activity_desc)!!
        mBtnCreateActivity = bottomSheetDialog.findViewById(R.id.btn_create_activity)!!
        mProgressBar = bottomSheetDialog.findViewById(R.id.pb_create_activity)!!

        // Initialize the result launcher to pick the image
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                if (uri != null) {
                    imageChosen = true
                    setImage(uri, mIvActivityAvatar)
                    uploadAcImgToFirebaseStorage(uri)
                }
            }
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

            mEtSelectedDate.setOnClickListener {
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
                            mEtSelectedDate.setText(eventDateValue.format(formatter))
                            // The last selected date is saved if the dialog is reopened
                            lastDate.set(year, month - 1, day)
                        }

                    }
                    // Show the picker and wait to reset the variable
                    fragmentManager?.let { it1 -> dateDialog!!.show(it1, "main_act_picker") }
                    Handler(Looper.getMainLooper()).postDelayed({ dateDialog = null }, 750)
                }
            }

            mIvExit.setOnClickListener {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mIvActivityAvatar.setOnClickListener {
                resultLauncher.launch("image/*")
            }

            mBtnCreateActivity.setOnClickListener {
                mTilActivityName.error = null
                mTilSelectedDate.error = null
                mTilActivityLocation.error = null
                mTilActivityDesc.error = null

                if (mEtActivityName.text.toString().trim().isEmpty()) {
                    mTilActivityName.error = getString(R.string.err_empty_field)
                    return@setOnClickListener
                }

                if (mEtSelectedDate.text.toString().trim().isEmpty()) {
                    mTilSelectedDate.error = getString(R.string.err_empty_field)
                    return@setOnClickListener
                }

                if (mEtActivityLocation.text.toString().trim().isEmpty()) {
                    mTilActivityLocation.error = getString(R.string.err_empty_field)
                    return@setOnClickListener
                }

                if (mEtActivityDesc.text.toString().trim().isEmpty()) {
                    mTilActivityDesc.error = getString(R.string.err_empty_field)
                    return@setOnClickListener
                }

                mProgressBar.visibility = View.VISIBLE

                val newActivity = Activity()
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                newActivity.aid = FirebaseFirestore.getInstance().collection(COLLECTION_ACTIVITIES).document().id
                newActivity.acTitle = mEtActivityName.text.toString().trim()
                newActivity.acDesc = mEtActivityDesc.text.toString().trim()
                newActivity.acLocation = mEtActivityLocation.text.toString().trim()
                newActivity.acTime = mEtSelectedDate.text.toString().trim()
                newActivity.acCreatedByID = firebaseUser!!.uid
                newActivity.acParticipants = 0;
                newActivity.acType = "virtual"
                newActivity.acCreatedAt = Timestamp(Date())
                newActivity.acImgRefs = urlForDocument.toString()

                DataManager.mInstance.createActivity(newActivity, firebaseUser, object : DataListener<Boolean> {
                    override fun onData(data: Boolean?, exception: java.lang.Exception?) {
                        if (data != null && data) {
                            LyveApplication.mInstance.activity = newActivity
                        } else {
                            Log.e(TAG, "data has problems")
                        }
                    }
                })

                mProgressBar.visibility = View.GONE
            }
        }

        return binding.root
    }

    // Set the chosen image in the circular image
    private fun setImage(data: Uri, imageView: ImageView) {
        var bitmap: Bitmap? = null
        try {
            if (Build.VERSION.SDK_INT < 29) {
                @Suppress("DEPRECATION")
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data)
            } else {
                val source = ImageDecoder.createSource(requireContext().contentResolver, data)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        }

        if (bitmap == null) return

        // Bitmap ready. Avoid images larger than 450*450
        var dimension: Int = getBitmapSquareSize(bitmap)
        if (dimension > 450) dimension = 450

        val resizedBitmap = ThumbnailUtils.extractThumbnail(
            bitmap,
            dimension,
            dimension,
            ThumbnailUtils.OPTIONS_RECYCLE_INPUT,
        )

        imageView.setImageBitmap(resizedBitmap)
    }

    private fun getBitmapSquareSize(bitmap: Bitmap): Int {
        return bitmap.width.coerceAtMost(bitmap.height)
    }

    private fun uploadAcImgToFirebaseStorage(imageUri: Uri) {
        val fileRef: StorageReference = FirebaseStorage.getInstance()
            .getReference(System.currentTimeMillis().toString() + getFileExtension(imageUri.toString()))
        fileRef.putFile(imageUri).addOnCompleteListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                urlForDocument = uri
            }
        }
    }
}