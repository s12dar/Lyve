package com.tech.lyve.ui.fragments.ping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tech.lyve.adapters.PingAdapter
import com.tech.lyve.databinding.FragmentPingBinding
import com.tech.lyve.models.BasketTypeUser
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.utils.Constants.QUERY_PAGE_SIZE
import com.tech.lyve.utils.OnboardingUtils
import com.tech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PingFragment : Fragment() {

    private lateinit var binding: FragmentPingBinding
    private val viewModel: PingViewModel by viewModels()
    private var mUser = User()
    private var mEvent = Event()
    private lateinit var pingAdapter: PingAdapter
    private var mParticipants = mutableListOf<BasketTypeUser>()

    private var isError = false
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                isScrolling = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPingBinding.inflate(inflater, container, false)
        setRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUser()
        manageChips()
    }

    private fun getParticipantsBelongingToUser(user: User): MutableList<BasketTypeUser> {
        if (mUser.nrOfEvents > 0) {
            viewModel.getEventBelongingToCurrentUser(user).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        (activity as OnboardingUtils).showProgressBar()
                    }
                    is Resource.Success -> {
                        (activity as OnboardingUtils).hideProgressBar()
                        result.data?.let {
                            mEvent = it[0]
                            pingAdapter.differ.submitList(mEvent.participants)
                            return@let mEvent.participants
                        }
                    }
                    is Resource.Error -> {
                        (activity as OnboardingUtils).hideProgressBar()
                        Snackbar.make(
                            requireView(),
                            "Oops, something went wrong!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return mutableListOf()
    }

    private fun getCurrentUser() {
        with(viewModel) {
            getCurrentUser().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            mUser = result.data
                            mParticipants = getParticipantsBelongingToUser(mUser)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun manageChips() {
        pingAdapter.apply {
            setOnAcceptClickListener { requestedUser ->
                if (isAcceptChecked) {
                    mEvent.participants[mEvent.participants.indexOf(requestedUser)].status =
                        "accepted"
                } else {
                    mEvent.participants[mEvent.participants.indexOf(requestedUser)].status =
                        "pending"
                }

                updateEvent()
            }
        }

        pingAdapter.setOnRejectClickListener { requestedUser ->
            if (pingAdapter.isDeclineChecked) {
                mEvent.participants[mEvent.participants.indexOf(requestedUser)].status = "declined"
            } else {
                mEvent.participants[mEvent.participants.indexOf(requestedUser)].status = "pending"
            }

            updateEvent()
        }
    }

    private fun updateEvent() {
        (activity as OnboardingUtils).showProgressBar()
        viewModel.updateEvent(mEvent, mUser)
            .observe(viewLifecycleOwner) { eventResult ->
                when (eventResult) {
                    is Resource.Success -> {
                        (activity as OnboardingUtils).hideProgressBar()
                        Snackbar.make(
                            requireView(),
                            "Yaay, we sent your request!",
                            Snackbar.LENGTH_SHORT
                        ).show()
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

    private fun setRecyclerView() {
        pingAdapter = PingAdapter(requireContext())
        binding.rvSearch.apply {
            adapter = pingAdapter
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(this@PingFragment.scrollListener)
        }
    }
}