package com.lyvetech.lyve.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.lyvetech.lyve.R
import com.lyvetech.lyve.adapters.SearchAdapter
import com.lyvetech.lyve.databinding.FragmentSearchBinding
import com.lyvetech.lyve.listeners.OnClickListener
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.SearchViewModel
import com.lyvetech.lyve.utils.OnboardingUtils
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

        viewModel.currentUser.observe(viewLifecycleOwner) {
            mUser = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Top App bar
        (activity as OnboardingUtils?)?.showAndSetTopAppBar("Search")
        manageTopBarNavigation()
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
                viewModel.searchActivities(searchQuery).observe(viewLifecycleOwner) {
                    val searchAdapter = SearchAdapter(
                        mUser,
                        mutableListOf(User()),
                        it,
                        VIEW_TYPE_ONE,
                        requireContext(),
                        this@SearchFragment
                    )
                    val linearLayoutManager = LinearLayoutManager(context)

                    binding.rvSearch.layoutManager = linearLayoutManager
                    binding.rvSearch.adapter = searchAdapter
                }
            }

            VIEW_TYPE_TWO -> {
                viewModel.searchUsers(searchQuery).observe(viewLifecycleOwner) {
                    val searchAdapter = SearchAdapter(
                        mUser,
                        it,
                        mutableListOf(Activity()),
                        VIEW_TYPE_TWO,
                        requireContext(),
                        this@SearchFragment
                    )
                    val linearLayoutManager = LinearLayoutManager(context)

                    binding.rvSearch.layoutManager = linearLayoutManager
                    binding.rvSearch.adapter = searchAdapter
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

    private fun manageTopBarNavigation() {
        (requireActivity().findViewById<View>(R.id.top_app_bar) as MaterialToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onPostClicked(activity: Activity) {}

    override fun onPostLongClicked(activity: Activity) {}

    override fun onUserClicked(user: User) {}

    override fun onUserFollowBtnClicked(user: User, isChecked: Boolean) {
        if (isChecked) {
            mUser.followings.add(user.uid)
            user.followers.add(mUser.uid)
        } else {
            mUser.followings.remove(user.uid)
            user.followers.remove(mUser.uid)
        }

        viewModel.updateUser(mUser)
        viewModel.updateUser(user)
    }
}