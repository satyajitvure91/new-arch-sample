package com.satyajit.codes.new_arch_sample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.satyajit.codes.new_arch_sample.databinding.LaunchDetailsFragmentBinding
import com.satyajit.codes.new_arch_sample.state.ViewState
import com.satyajit.codes.new_arch_sample.viewmodel.LaunchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LaunchDetailsFragment : Fragment() {

    private val TAG = "LaunchDetailsFragment"
    private lateinit var binding: LaunchDetailsFragmentBinding
    private val args: LaunchDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<LaunchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LaunchDetailsFragmentBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.queryLaunchDetails(args.id)
        configureButton()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.launchDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.error.visibility = View.GONE
                }
                is ViewState.Success -> {
                    if (response.result.launch == null) {
                        binding.progressBar.visibility = View.GONE
                        binding.error.visibility = View.VISIBLE
                    } else {
                        binding.launchDetail = response.result
                        binding.progressBar.visibility = View.GONE
                        binding.error.visibility = View.GONE
                    }
                }
                is ViewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun configureButton() {
        binding.bookButton.setOnClickListener {
            viewModel.mutateTripBookDetails(listOf(args.id))
        }
    }
}