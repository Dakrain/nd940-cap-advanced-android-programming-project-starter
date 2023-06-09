package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.domain.model.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.utils.PermissionUtils
import com.example.android.politicalpreparedness.utils.bindFadeVisible
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Locale

class DetailFragment : BaseFragment() {

    companion object {
        const val REQUEST_PERMISSION_LOCATION = 1
    }


    override val _viewModel by viewModel<RepresentativeViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepresentativeBinding.inflate(inflater)
        val adapter = RepresentativeListAdapter()
        binding.rclMyRepresentatives.adapter = adapter
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        _viewModel.representatives.observe(viewLifecycleOwner) {
            it?.let {
                binding.rclMyRepresentatives.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        }
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
        binding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                _viewModel.updateState(binding.spnState.selectedItem as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                _viewModel.updateState(binding.spnState.selectedItem as String)
            }
        }
        binding.btnSearch.setOnClickListener {
            _viewModel.getAddressFromFields()
            _viewModel.findMyRepresentatives()
        }

        binding.btnMyLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                enableLocationService(false)
            }
        }
        return binding.root
    }

    private fun selectSpinnerState(address: Address) {
        val states = resources.getStringArray(R.array.states)
        binding.spnState.setSelection(
            if (states.contains(address.state)) {
                states.indexOf(address.state)
            } else {
                0
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {

            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(R.string.required_permisson),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.setting) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            enableLocationService()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            Timber.d("Missing permission")
            requestLocationPermission()
            false
        }
    }

    private fun requestLocationPermission() {
        if (isPermissionGranted()) return

        val permissionsArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val requestCode = REQUEST_PERMISSION_LOCATION

        requestPermissions(
            permissionsArray,
            requestCode
        )
    }


    private fun isPermissionGranted(): Boolean {
        return PermissionUtils.isGranted(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) && PermissionUtils.isGranted(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun enableLocationService(needResolve: Boolean = true) {
        Timber.d("enableLocationService")
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        )
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Timber.d("Location service enabled")
            getLocation()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && needResolve) {
                Timber.d("Location service need resolve")
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_PERMISSION_LOCATION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Timber.d("Location service denied")
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.location_required_error),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        enableLocationService()
                    }.show()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Timber.d("Get location")

        //Get location from LocationServices
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                Timber.d("Location founded: $location")
                val address = geoCodeLocation(location)
                if (address != null) {
                    selectSpinnerState(address)
                    _viewModel.updateAddress(address)
                } else {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.cant_fetch_location),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val result = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first()

        return result
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}