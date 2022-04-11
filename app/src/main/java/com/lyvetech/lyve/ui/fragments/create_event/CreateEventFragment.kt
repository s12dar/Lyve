package com.lyvetech.lyve.ui.fragments.create_event

import android.app.Activity
import android.content.Intent
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
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.lyvetech.lyve.LyveApplication
import com.lyvetech.lyve.R
import com.lyvetech.lyve.databinding.FragmentCreateEventBinding
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.fragments.home.HomeFragment
import com.lyvetech.lyve.utils.Constants
import com.lyvetech.lyve.utils.OnboardingUtils
import com.lyvetech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var TAG = HomeFragment::class.qualifiedName
    private val viewModel: CreateEventViewModel by viewModels()
    private lateinit var binding: FragmentCreateEventBinding
    private lateinit var imageResultLauncher: ActivityResultLauncher<String>
    private lateinit var placeResultLauncher: ActivityResultLauncher<Intent>
    private var imageChosen = false
    private var localImgUri: Uri? = null
    private var mUser = User()
    private var mEvent = Event()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

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
                editable === binding.etName.editableText -> {
                    val acName = binding.etName.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilName.error = null
                    }
                }

                editable === binding.etLocation.editableText -> {
                    val acName = binding.etLocation.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilLocation.error = null
                    }
                }

                editable === binding.etDesc.editableText -> {
                    val acName = binding.etDesc.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilDesc.error = null
                    }
                }

                editable === binding.etEventUrl.editableText -> {
                    val acName = binding.etEventUrl.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilEventUrl.error = null
                    }
                }

                editable === binding.etStartDate.editableText -> {
                    val acName = binding.etStartDate.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilStartDate.error = null
                    }
                }

                editable === binding.etStartTime.editableText -> {
                    val acName = binding.etStartTime.text.toString().trim()
                    if (acName.isNotEmpty()) {
                        binding.tilStartTime.error = null
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeImageResultLauncher()
        initializePlaceResultLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        (activity as OnboardingUtils).showTopAppBar("Create event")
        (activity as OnboardingUtils).hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUser()
        initializePlaces()
        manageBindingViews()
        manageTopBarNavigation()
    }

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.toolbar)
                as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun manageBindingViews() {
        with(binding) {
            etStartDate.setOnClickListener {
                manageDateSelection(etStartDate)
            }
            etStartTime.setOnClickListener {
                manageTimeSelection(etStartTime)
            }
            ivPhoto.setOnClickListener {
                imageResultLauncher.launch("image/*")
            }

            swOnlineEvent.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.tilLocation.visibility = View.INVISIBLE
                    binding.tilEventUrl.visibility = View.VISIBLE
                } else {
                    binding.tilLocation.visibility = View.VISIBLE
                    binding.tilEventUrl.visibility = View.INVISIBLE
                }
            }

            btnCreateActivity.setOnClickListener {
                manageEventCreation()
            }
        }
    }

    // Manage date selection process
    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageDateSelection(editText: EditText) {
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
                    .setTitleText(R.string.title_select_date)
                    .setSelection(lastDate.timeInMillis)
                    .setCalendarConstraints(constraints)
                    .build()

            // The user pressed ok
            dateDialog.addOnPositiveButtonClickListener {
                val selection = it
                selection?.let {
                    val date = Calendar.getInstance()
                    // Use a standard timezone to avoid wrong date on different time zones
                    date.timeZone = TimeZone.getTimeZone("UTC")
                    date.timeInMillis = selection
                    val year = date.get(Calendar.YEAR)
                    val month = date.get(Calendar.MONTH) + 1
                    val day = date.get(Calendar.DAY_OF_MONTH)
                    eventDateValue = LocalDate.of(year, month, day)
                    editText.setText(eventDateValue.format(formatter))
                    // The last selected date is saved if the dialog is reopened
                    lastDate.set(year, month - 1, day)
                }
            }
            // Show the picker and wait to reset the variable
            activity?.supportFragmentManager?.let { it1 ->
                dateDialog?.show(
                    it1,
                    "main_act_picker"
                )
            }
            Handler(Looper.getMainLooper()).postDelayed({ dateDialog = null }, 750)
        }
    }

    private fun createEvent() {
        (activity as OnboardingUtils).showProgressBar()
        with(viewModel) {
            localImgUri?.let {
                getImgUri(it).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Success -> {
                            mEvent.acImgRefs = result.data.toString()
                            createEvent(mEvent, mUser)
                                .observe(viewLifecycleOwner) { eventResult ->
                                    when (eventResult) {
                                        is Resource.Success -> {
                                            findNavController().navigate(R.id.action_createEventFragment_to_homeFragment)
                                            (activity as OnboardingUtils).hideProgressBar()
                                        }
                                        is Resource.Error -> {
                                            (activity as OnboardingUtils).hideProgressBar()
                                            Snackbar.make(
                                                requireView(),
                                                "Oops, something went wrong!",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> {}
                                    }
                                }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getCurrentUser() {
        with(viewModel) {
            getCurrentUser().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            mUser = result.data
                            LyveApplication.mInstance.currentUser = mUser
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun manageTimeSelection(editText: EditText) {
        val timeDialog =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(R.string.title_select_time)
                .build()

        timeDialog.addOnPositiveButtonClickListener {
            val time = "${timeDialog.hour} : ${timeDialog.minute}"
            editText.setText(time)
        }

        // Show the picker
        activity?.supportFragmentManager?.let {
            timeDialog.show(it, "main_act_picker")
        }
    }

    private fun initializeImageResultLauncher() {
        imageResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                if (uri != null) {
                    imageChosen = true
                    setImage(uri, binding.ivPhoto)
                    localImgUri = uri
                }
                binding.tvSelectImage.visibility = View.INVISIBLE
            }
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

    private fun initializePlaceResultLauncher() {
        placeResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val place: Place = Autocomplete.getPlaceFromIntent(it.data)
                    binding.etLocation.setText(place.address)
                    mEvent.acLocation.put(
                        place.address,
                        GeoPoint(place.latLng.latitude, place.latLng.longitude)
                    )
                }
            }
    }

    private fun initializePlaces() {
        Places.initialize(context, "AIzaSyD1XxkbKiroHe9kDY-JbvSjEeR_L0aUEM0")
        binding.etLocation.setOnClickListener {
            val fieldList: List<Place.Field> = mutableListOf(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(context)
            placeResultLauncher.launch(intent)
        }
    }

    private fun manageEventCreation() {
        val eventName = binding.etName.text.toString()
        val eventDesc = binding.etDesc.text.toString()
        val eventDate = binding.etStartDate.text.toString()
        val eventTime = binding.etStartTime.text.toString()
        val eventLocation = binding.etLocation.text.toString()
        val isEventOnline = binding.swOnlineEvent.isChecked

        if (eventName.isBlank()) {
            binding.tilName.error =
                getString(R.string.err_empty_field)
            return
        }
        if (eventDesc.isBlank()) {
            binding.tilDesc.error =
                getString(R.string.err_empty_field)
            return
        }
        if (eventDate.isBlank()) {
            binding.tilStartDate.error =
                getString(R.string.err_empty_field)
            return
        }
        if (eventTime.isBlank()) {
            binding.tilStartTime.error =
                getString(R.string.err_empty_field)
            return
        }
        if (eventLocation.isBlank()) {
            binding.tilLocation.error =
                getString(R.string.err_empty_field)
            return
        }

        mEvent.apply {
            aid = firestore.collection(Constants.COLLECTION_ACTIVITIES).document().id
            acTitle = eventName
            acDesc = eventDesc
            isOnline = isEventOnline
            acCreatedAt = Timestamp(Date())
            acCreatedByID = mUser.uid
            acParticipants = mutableListOf()
        }
        createEvent()
    }
}