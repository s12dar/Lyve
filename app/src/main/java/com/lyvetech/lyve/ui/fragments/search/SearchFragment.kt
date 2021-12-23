package com.lyvetech.lyve.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyvetech.lyve.adapters.SearchAdapter
import com.lyvetech.lyve.databinding.FragmentSearchBinding
import com.lyvetech.lyve.listeners.OnPostClickListener
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.ui.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), OnPostClickListener {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

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
        manageSearch(VIEW_TYPE_ONE)

        binding.chipGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.chipEvents.isChecked) {
                manageSearch(VIEW_TYPE_ONE)
            } else {
                manageSearch(VIEW_TYPE_TWO)
            }
        }
    }

    private fun manageSearch(viewType: Int) {
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    when (viewType) {
                        VIEW_TYPE_ONE -> {
                            viewModel.searchActivities(newText).observe(viewLifecycleOwner) {
                                val searchAdapter = SearchAdapter(
                                    mutableListOf(User()),
                                    it,
                                    1,
                                    requireContext(),
                                    this@SearchFragment
                                )
                                val linearLayoutManager = LinearLayoutManager(context)

                                binding.rvSearch.layoutManager = linearLayoutManager
                                binding.rvSearch.adapter = searchAdapter
                            }
                        }

                        VIEW_TYPE_TWO -> {
                            viewModel.searchUsers(newText).observe(viewLifecycleOwner) {
                                val searchAdapter = SearchAdapter(
                                    it,
                                    mutableListOf(Activity()),
                                    2,
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
                return true
            }

        })
    }

    override fun onPostClicked(item: Any) {
        TODO("Not yet implemented")
    }

    override fun onPostLongClicked(activity: Activity) {
        TODO("Not yet implemented")
    }
}