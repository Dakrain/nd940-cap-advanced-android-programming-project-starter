package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : BaseFragment() {

    override val _viewModel by viewModel<ElectionsViewModel>()

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        // ViewModel values and create ViewModel
        binding.viewModel = _viewModel


        val upComingAdapter = ElectionListAdapter(ElectionListener { election ->
            _viewModel.navigateToVoterInfo(election)
        })
        val savedAdapter = ElectionListAdapter(ElectionListener { election ->
            _viewModel.navigateToVoterInfo(election)
        })

        _viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                upComingAdapter.submitList(it)
            }
        }

        _viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let {
                savedAdapter.submitList(it)
            }
        }

        binding.rclUpcomingElections.adapter = upComingAdapter
        binding.rclSavedElections.adapter = savedAdapter
        return binding.root
    }

    //Popback when click home

}