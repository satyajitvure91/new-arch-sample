package com.satyajit.codes.new_arch_sample.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.satyajit.codes.new_arch_sample.databinding.FragmentLaunchListBinding
import com.satyajit.codes.new_arch_sample.state.ViewState
import com.satyajit.codes.new_arch_sample.view.adapter.LaunchListAdapter
import com.satyajit.codes.new_arch_sample.viewmodel.LaunchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchesListFragment : Fragment(){

    private val TAG = "LaunchesListFragment"
    private lateinit var binding: FragmentLaunchListBinding
    private val launchListAdapter by lazy { LaunchListAdapter() }
    private val viewModel by viewModels<LaunchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.launches.adapter = launchListAdapter

        viewModel.queryLaunchList()
        observeLiveData()
        launchListAdapter.onItemClicked = {launch->
            launch.let {
                if (launch.id.isNotBlank()) {
                    findNavController().navigate(
                        LaunchesListFragmentDirections.actionLaunchesListFragmentToLaunchDetailsFragment(
                            id = launch.id
                        )
                    )
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.launchList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "observeLiveData: Loading")
                    binding.launches.visibility = View.GONE
                    binding.launchesFetchProgress.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    binding.launchesFetchProgress.visibility = View.GONE
                    if (response.result.launches.launches.isEmpty()) {
                        launchListAdapter.submitList(emptyList())
                        binding.launches.visibility = View.GONE
                        binding.launchesEmptyText.visibility = View.VISIBLE
                    } else {
                        binding.launches.visibility = View.VISIBLE
                        binding.launchesEmptyText.visibility = View.GONE
                    }
                    val results = response.result.launches.launches
                    Log.d(TAG, "observeLiveData: Success ${results.size}" )
                    launchListAdapter.submitList(results)
                }
                is ViewState.Error -> {
                    Log.e(TAG, "observeLiveData: Error")
                    launchListAdapter.submitList(emptyList())
                    binding.launchesFetchProgress.visibility = View.GONE
                    binding.launches.visibility = View.GONE
                    binding.launchesEmptyText.visibility = View.VISIBLE
                }
            }
        }
    }
}