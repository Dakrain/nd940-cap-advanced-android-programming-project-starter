package com.example.android.politicalpreparedness.domain.model

import com.example.android.politicalpreparedness.domain.model.Office
import com.example.android.politicalpreparedness.domain.model.Official

data class Representative (
        val official: Official,
        val office: Office
)