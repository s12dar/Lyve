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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.HomeAdapter
import com.lyvetech.lyve.databinding.FragmentHomeBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.HomeViewModel
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_FOLLOWER
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_FOLLOWING
import com.lyvetech.lyve.utils.Constants.Companion.BUNDLE_KEY
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.OnboardingUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment(), OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private var TAG = HomeFragment::class.qualifiedName

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    private var mUser = User()

    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private var imageChosen = false

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    // Vars for views in bottom sheet layout
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
    private var localImgUri: Uri? = null

    // Validate each field in the form with the same watcher
    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun afterTextChanged(editable: Editable) {
            when {
                editable === mEtActivityName.editableText -> {
                    val acName = mEtActivityName.text.toString().trim()
                    if (acName.isNotBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        mTilActivityName.error = null
                    }
                }

                editable === mEtActivityLocation.editableText -> {
                    val acName = mEtActivityLocation.text.toString().trim()
                    if (acName.isNotBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        mTilActivityLocation.error = null
                    }
                }

                editable === mEtActivityDesc.editableText -> {
                    val acName = mEtActivityDesc.text.toString().trim()
                    if (acName.isNotBlank()) {
                        // Setting the error on the layout is important to make the properties work. Kotlin synthetics are being used here
                        mTilActivityDesc.error = null
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    localImgUri = uri
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Deal with app bar from main activity
        (activity as OnboardingUtils?)?.hideTopAppBar()

        viewModel.allUsers.observe(viewLifecycleOwner) {
            LyveApplication.mInstance.allUsers = it as MutableList<User>
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Watch the fields in real time
        mEtActivityName.addTextChangedListener(watcher)
        mEtActivityLocation.addTextChangedListener(watcher)
        mEtActivityDesc.addTextChangedListener(watcher)

        manageHomeUI()
        subscribeUI(VIEW_TYPE_ONE)
        manageNavigationView()

        binding.fabAdd.setOnClickListener {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()

            mEtSelectedDate.setOnClickListener { manageDateSelection() }
            mIvExit.setOnClickListener { hideBottomSheet() }
            mIvActivityAvatar.setOnClickListener { resultLauncher.launch("image/*") }
            mBtnCreateActivity.setOnClickListener { manageActivityCreation() }
        }

        binding.drawerLayout.setOnRefreshListener {
            if (binding.chipFollowing.isChecked) {
                subscribeUI(VIEW_TYPE_ONE)
            } else {
                subscribeUI(VIEW_TYPE_TWO)
            }
            binding.drawerLayout.isRefreshing = false
        }

        binding.chipGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.chipFollowing.isChecked) {
                subscribeUI(VIEW_TYPE_ONE)
            } else {
                subscribeUI(VIEW_TYPE_TWO)
            }
        }

        manageToolBar()
        manageBottomSheetBehaviour()
    }

    private fun manageActivityCreation() {

        if (mEtActivityName.text.toString().trim().isEmpty()) {
            mTilActivityName.error = getString(R.string.err_empty_field)
            return
        }

        if (mEtSelectedDate.text.toString().trim().isEmpty()) {
            mTilSelectedDate.error = getString(R.string.err_empty_field)
            return
        }

        if (mEtActivityLocation.text.toString().trim().isEmpty()) {
            mTilActivityLocation.error = getString(R.string.err_empty_field)
            return
        }

        if (mEtActivityDesc.text.toString().trim().isEmpty()) {
            mTilActivityDesc.error = getString(R.string.err_empty_field)
            return
        }

        if (localImgUri != null) {
            uploadAcImgToFirebaseStorage(localImgUri!!)
        }

        mProgressBar.visibility = View.VISIBLE

        val newActivity = Activity()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        newActivity.aid =
            FirebaseFirestore.getInstance().collection(COLLECTION_ACTIVITIES).document().id
        newActivity.acTitle = mEtActivityName.text.toString().trim()
        newActivity.acDesc = mEtActivityDesc.text.toString().trim()
        newActivity.acLocation = mEtActivityLocation.text.toString().trim()
        newActivity.acTime = mEtSelectedDate.text.toString().trim()
        newActivity.acCreatedByID = firebaseUser!!.uid
        newActivity.acParticipants = mutableListOf()
        newActivity.acType = "virtual"
        newActivity.acCreatedAt = Timestamp(Date())

        Handler(Looper.getMainLooper()).postDelayed({
            if (urlForDocument != null) {
                newActivity.acImgRefs = urlForDocument.toString()
            }

            viewModel.createActivity(newActivity, mUser)

            mProgressBar.visibility = View.GONE
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }, 7000)
    }

    // Manage date selection process
    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageDateSelection() {
        var dateDialog: MaterialDatePicker<Long>? = null
        val endDate = Calendar.getInstance()
        endDate.set(2022, 12, 31)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val startDate = Calendar.getInstance()

        // To automatically show the last selected date, parse it to another Calendar object
        val lastDate = Calendar.getInstance()

        var eventDateValue: LocalDate

        if (dateDialog == null) {
            // Build constraints
            val constraints =
                CalendarConstraints.Builder()
                    .setStart(startDate.timeInMillis)
                    .setEnd(endDate.timeInMillis)
                    .setValidator(DateValidatorPointForward.now())
                    .build()

            // Build the dialog itself
            dateDialog =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.txt_activity_date)
                    .setSelection(lastDate.timeInMillis)
                    .setCalendarConstraints(constraints)
                    .build()

            // The user pressed ok
            dateDialog.addOnPositiveButtonClickListener {
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

    private fun hideBottomSheet() {
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        mEtActivityName.setText("")
        mEtActivityDesc.setText("")
        mEtActivityLocation.setText("")
        mEtSelectedDate.setText("")
        mIvActivityAvatar.setImageResource(R.drawable.ic_upload_image)
        localImgUri = null
    }

    @SuppressLint("SetTextI18n")
    private fun manageHomeUI() {
        val header = binding.navView.getHeaderView(0)
        val tvName = header.findViewById<TextView>(R.id.tv_name)
        val tvBio = header.findViewById<TextView>(R.id.tv_bio)
        val tvFollowers = header.findViewById<TextView>(R.id.tv_followers)
        val tvFollowing = header.findViewById<TextView>(R.id.tv_following)

        viewModel.currentUser.observe(viewLifecycleOwner) {
            tvName.text = it.name
            tvBio.text = "Everything will be ok"
            tvFollowers.text = "${it.followers.size} FOLLOWERS"
            tvFollowing.text = "${it.followings.size} FOLLOWINGS"
        }

        val bundle = Bundle()

        tvFollowers.setOnClickListener {
            bundle.putString(BUNDLE_KEY, BUNDLE_FOLLOWER)
            findNavController().navigate(R.id.action_homeFragment_to_followingFragment, bundle)
        }
        tvFollowing.setOnClickListener {
            bundle.putString(BUNDLE_KEY, BUNDLE_FOLLOWING)
            findNavController().navigate(R.id.action_homeFragment_to_followingFragment, bundle)
        }
    }

    private fun subscribeUI(viewType: Int) {
        when (viewType) {
            VIEW_TYPE_ONE -> {
                viewModel.allActivities.observe(viewLifecycleOwner) { activities ->
                    activities?.let {
                        binding.rvActivity.apply {
                            adapter = HomeAdapter(
                                activities,
                                requireContext(),
                                this@HomeFragment
                            )
                            layoutManager = LinearLayoutManager(context)
                        }
                    }
                }
            }

            VIEW_TYPE_TWO -> {
                viewModel.currentUser.observe(viewLifecycleOwner) { user ->
                    viewModel.getFollowingActivities(user)
                        .observe(viewLifecycleOwner) { activities ->
                            activities?.let {
                                binding.rvActivity.apply {
                                    adapter = HomeAdapter(
                                        activities,
                                        requireContext(),
                                        this@HomeFragment
                                    )
                                    layoutManager = LinearLayoutManager(context)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun manageBottomSheetBehaviour() {
        bottomSheetDialog.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //Here listen all of action bottom sheet
                Log.i(TAG, "onStateChanged")
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mEtActivityName.setText("")
                    mEtActivityDesc.setText("")
                    mEtActivityLocation.setText("")
                    mEtSelectedDate.setText("")
                    mIvActivityAvatar.setImageResource(R.drawable.ic_upload_image)
                    localImgUri = null
                }
            }
        })
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

    private fun manageToolBar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.dlHome.openDrawer(GravityCompat.START)
        }
    }

    private fun manageNavigationView() {
        val navigation = requireView().findViewById<NavigationView>(R.id.nav_view)
        navigation.bringToFront()
        navigation.setNavigationItemSelectedListener(this@HomeFragment)
    }

    private fun getBitmapSquareSize(bitmap: Bitmap): Int {
        return bitmap.width.coerceAtMost(bitmap.height)
    }

    private fun uploadAcImgToFirebaseStorage(imageUri: Uri) {
        val fileRef: StorageReference = FirebaseStorage.getInstance()
            .getReference(
                System.currentTimeMillis().toString()
            )
        fileRef.putFile(imageUri).addOnCompleteListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                urlForDocument = uri
            }
        }
    }

    override fun onPostClicked(activity: Activity) {
        LyveApplication.mInstance.activity = activity
        findNavController().navigate(R.id.action_homeFragment_to_homeInfoFragment)
    }

    override fun onPostLongClicked(activity: Activity) {}

    override fun onUserClicked(user: User) {}

    override fun onUserFollowBtnClicked(user: User, isChecked: Boolean) {}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_chat -> {}
            R.id.menu_search -> {}
            R.id.menu_notification -> {}
        }

        binding.dlHome.closeDrawer(GravityCompat.START)
        return true
    }
}