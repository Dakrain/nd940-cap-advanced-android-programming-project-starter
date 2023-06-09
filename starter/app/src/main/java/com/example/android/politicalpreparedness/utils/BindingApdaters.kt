package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.domain.model.State
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("dateText")
fun TextView.bindElectionDateText(date: Date?) {
    text = if (date == null) {
        ""
    } else {
        val format = SimpleDateFormat("EEEE, MMM. dd, yyyy • HH:mm z", Locale.US)
        format.format(date)
    }
}

@BindingAdapter("followText")
fun TextView.bindFollowText(isFollow: Boolean) {
    text = if (isFollow) {
        context.getString(R.string.unfollow_election)
    } else {
        context.getString(R.string.follow_election)
    }
}

@BindingAdapter("correspondenceAddress")
fun TextView.bindCorrespondenceAddress(states: List<State>?) {
    val address =
        states?.firstOrNull()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
    Timber.d("address: $address")
    if (address.isNullOrEmpty()) {
        this.visibility = TextView.GONE
    } else {
        this.visibility = TextView.VISIBLE
        text = address
    }
}


@BindingAdapter("fadeVisible")
fun View.bindFadeVisible(showLoading: Boolean = false) {
    if(showLoading) {
        visibility = View.VISIBLE
        fadeOut()
    } else {
        visibility = View.GONE
        fadeIn()
    }
}

