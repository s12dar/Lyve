package com.tech.lyve.ui.fragments.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tech.lyve.LyveApplication
import com.tech.lyve.adapters.SearchAdapter
import com.tech.lyve.databinding.FragmentSearchBinding
import com.tech.lyve.listeners.OnClickListener
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.utils.OnboardingUtils
import com.tech.lyve.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), OnClickListener {

    private val TAG = SearchFragment::class.qualifiedName
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    private var mUser = User()

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUser()
        manageSearch(VIEW_TYPE_ONE)
        manageChipGroup()
    }

    private fun manageSearch(viewType: Int) {
        handlingSearchProcedure(viewType, binding.svSearch.query.toString())
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    handlingSearchProcedure(viewType, newText)
                }
                return true
            }

        })
    }

    private fun handlingSearchProcedure(viewType: Int, searchQuery: String) {
        when (viewType) {
            VIEW_TYPE_ONE -> {
                with(viewModel) {
                    getSearchedActivities(searchQuery).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Resource.Loading -> {
                                (activity as OnboardingUtils).showProgressBar()
                            }
                            is Resource.Success -> {
                                (activity as OnboardingUtils).hideProgressBar()
                                result.data?.let {
                                    binding.rvSearch.apply {
                                        adapter = SearchAdapter(
                                            mUser,
                                            mutableListOf(User()),
                                            it,
                                            VIEW_TYPE_ONE,
                                            requireContext(),
                                            this@SearchFragment
                                        )
                                        layoutManager = LinearLayoutManager(context)
                                    }
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
            }

            VIEW_TYPE_TWO -> {
                with(viewModel) {
                    getSearchedUsers(searchQuery).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Resource.Loading -> {
                                (activity as OnboardingUtils).showProgressBar()
                            }
                            is Resource.Success -> {
                                (activity as OnboardingUtils).hideProgressBar()
                                result.data?.let {
                                    binding.rvSearch.apply {
                                        adapter = SearchAdapter(
                                            mUser,
                                            it,
                                            mutableListOf(Event()),
                                            VIEW_TYPE_TWO,
                                            requireContext(),
                                            this@SearchFragment
                                        )
                                        layoutManager = LinearLayoutManager(context)
                                    }
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
            }
        }
    }

    private fun manageChipGroup() {
        binding.chipGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.chipEvents.isChecked) {
                manageSearch(VIEW_TYPE_ONE)
            } else {
                manageSearch(VIEW_TYPE_TWO)
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

    override fun onPostClicked(event: Event) {}

    override fun onPostLongClicked(event: Event) {}

    override fun onUserClicked(user: User) {}

    override fun onUserFollowBtnClicked(user: User, isChecked: Boolean) {
        if (isChecked) {
            mUser.followings.add(user.uid)
            user.followers.add(mUser.uid)
        } else {
            mUser.followings.remove(user.uid)
            user.followers.remove(mUser.uid)
        }

        with(viewModel) {
            updateUser(mUser).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> Log.d(TAG, "user is updated")
                    else -> {}
                }
            }
            updateUser(user).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> Log.d(TAG, "user is updated")
                    else -> {}
                }
            }
        }
    }
}