package com.example.android.politicalpreparedness.data.network

import com.example.android.politicalpreparedness.data.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.data.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.domain.model.ElectionResponse
import com.example.android.politicalpreparedness.domain.model.RepresentativeResponse
import com.example.android.politicalpreparedness.domain.model.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(DateAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    @GET("voterinfo")
    suspend fun getVoterInfo(
        @Query("address") address: String,
        @Query("electionId") electionId: Int?,
        @Query("officialOnly") officialOnly: Boolean = false
    ): VoterInfoResponse

    @GET("representatives")
    suspend fun getRepresentatives(
        @Query("ocdId") ocdId: String,
        @Query("levels") levels: String?,
        @Query("roles") roles: String?,
        @Query("recursive") recursive: Boolean = false
    ): RepresentativeResponse

}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}