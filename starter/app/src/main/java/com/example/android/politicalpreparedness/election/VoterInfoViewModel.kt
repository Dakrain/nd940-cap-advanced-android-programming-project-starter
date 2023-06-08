package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.data.database.Result
import com.example.android.politicalpreparedness.domain.model.Election
import com.example.android.politicalpreparedness.domain.model.VoterInfoResponse
import com.example.android.politicalpreparedness.domain.repository.ElectionRemoteRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(
    app: Application,
    private val dataSource: ElectionDao,
    private val remoteRepository: ElectionRemoteRepository,
) : BaseViewModel(app) {
    private val _isFollowed = MutableLiveData<Boolean>(false)
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */


    fun getVoterInfo(election: Election) {
        _voterInfo.value = VoterInfoResponse(election = election)
        viewModelScope.launch {
            val result =
                remoteRepository.getVoter(election.division.getAddress(), election.id.toInt())
            when (result) {
                is Result.Success -> {
                    _voterInfo.value = result.data
                }
                is Result.Error -> {
                    showToast.value = result.message
                }
            }
        }
    }

    private fun saveVoterInfo() {
        _isFollowed.value = true
        viewModelScope.launch {
            dataSource.insertElection(voterInfo.value!!.election)
        }
    }

    private fun deleteVoterInfo() {
        _isFollowed.value = false
        viewModelScope.launch {
            dataSource.deleteElectionById(voterInfo.value!!.election.id.toInt())
        }
    }

    fun checkIsFollowing() {
        viewModelScope.launch {
            _isFollowed.value =
                dataSource.getElectionById(voterInfo.value!!.election.id.toInt()) != null
            Timber.d("isFollowed: ${isFollowed.value}")
        }
    }

    fun followOnClick() {
        if (isFollowed.value == true) {
            deleteVoterInfo()
        } else {
            saveVoterInfo()
        }
    }
}