package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.database.succeeded
import com.example.android.politicalpreparedness.domain.model.Representative
import com.example.android.politicalpreparedness.domain.repository.ElectionRemoteRepository
import com.example.android.politicalpreparedness.data.database.Result
import com.example.android.politicalpreparedness.domain.model.Address

import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(
    app: Application,
    private val remoteRepository: ElectionRemoteRepository
) : BaseViewModel(app) {

    private val _representatives = MutableLiveData<List<Representative>>()

    val representatives: MutableLiveData<List<Representative>>
        get() = _representatives

    val address1 = MutableLiveData<String>()
    val address2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun findMyRepresentatives() {
        val address = getAddressFromFields()
        showLoading.value = true
        _representatives.value = emptyList()
        viewModelScope.launch {
            val result = remoteRepository.getRepresentatives(address = address.toFormattedString())
            Timber.d("address: ${address.toFormattedString()}")
            showLoading.value = false
            when (result) {
                is Result.Success -> {
                    _representatives.value = result.data
                }

                is Result.Error -> {
                    showSnackBar.value = result.message
                    _representatives.value = emptyList()
                }
            }
        }
    }

    fun updateState(newState: String) {
        state.value = newState
    }

    fun getAddressFromFields(): Address = Address(
        address1.value.toString(),
        address2.value.toString(),
        city.value.toString(),
        state.value.toString(),
        zip.value.toString()
    )

    fun updateAddress(address: Address) {
        address1.value = address.line1
        address2.value = address.line2 ?: ""
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
    }
}
