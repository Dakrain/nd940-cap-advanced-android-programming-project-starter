package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class VoterInfoFragment : BaseFragment() {

    override val _viewModel by viewModel<VoterInfoViewModel>()
    private lateinit var binding: FragmentVoterInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View? {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        val bundle = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val election = bundle.argElection

        _viewModel.getVoterInfo(election)
        _viewModel.checkIsFollowing()

        _viewModel.voterInfo.observe(viewLifecycleOwner) {
            it?.state?.get(0)?.electionAdministrationBody?.let { body ->
                Timber.d("ballotUrl: ${body.ballotInfoUrl}")
                Timber.d("votingLocationUrl: ${body.votingLocationFinderUrl}")

                val ballotUrl = body.ballotInfoUrl
                val votingLocationUrl = body.votingLocationFinderUrl
                binding.stateBallot.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(ballotUrl)
                    startActivity(intent)
                }

                binding.stateLocations.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(votingLocationUrl)
                    startActivity(intent)
                }
            }
        }
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this


        // TODO: Add binding values

        // TODO: Populate voter info -- hide views without provided data.

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */

        // TODO: Handle loading of URLs

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }


}