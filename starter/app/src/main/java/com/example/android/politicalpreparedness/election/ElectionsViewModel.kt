package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.database.Result
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.domain.model.Election
import com.example.android.politicalpreparedness.domain.repository.ElectionLocalRepository
import com.example.android.politicalpreparedness.domain.repository.ElectionRemoteRepository
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val app: Application,
    private val remoteRepository: ElectionRemoteRepository,
    private val localRepository: ElectionLocalRepository
) : BaseViewModel(app) {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: MutableLiveData<List<Election>>
        get() = _upcomingElections

     val savedElections = localRepository.getElections()

    init {
        viewModelScope.launch {
            getUpcomingElections()
        }
    }

    fun navigateToVoterInfo(election: Election) {
        navigationCommand.value = NavigationCommand.To(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election
            )
        )
    }

    private suspend fun getUpcomingElections() {
        viewModelScope.launch {
            try {
                when (val result = remoteRepository.getElections()) {
                    is Result.Success<List<Election>> -> {
                        _upcomingElections.value = result.data
                    }

                    is Result.Error -> {
                        Timber.e("Get upcoming error: ${result.message}")
                        showSnackBar.value = result.message
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                showSnackBar.value = e.message
            }
        }
    }


}