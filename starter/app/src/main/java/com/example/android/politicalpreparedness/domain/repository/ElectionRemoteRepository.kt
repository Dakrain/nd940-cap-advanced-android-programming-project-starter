package com.example.android.politicalpreparedness.domain.repository

import com.example.android.politicalpreparedness.data.database.Result
import com.example.android.politicalpreparedness.data.network.CivicsApiService
import com.example.android.politicalpreparedness.domain.model.Election
import com.example.android.politicalpreparedness.domain.model.Representative
import com.example.android.politicalpreparedness.domain.model.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

interface ElectionRemoteDataSource {
    suspend fun getElections(): Result<List<Election>>
    suspend fun getVoter(address: String, electionId: Int): Result<VoterInfoResponse>
    suspend fun getRepresentatives(address: String): Result<List<Representative>>
}

class ElectionRemoteRepository(
    private val service: CivicsApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionRemoteDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        return withContext(dispatcher) {
            try {
                val response = service.getElections()
                Result.Success(response.elections)
            } catch (ex: Exception) {
                Result.Error(ex.message)
            }
        }
    }

    override suspend fun getVoter(address: String, electionId: Int): Result<VoterInfoResponse> {
        return withContext(dispatcher) {
            try {
                val response = service.getVoterInfo(
                    address = address,
                    electionId = electionId,
                )
                Result.Success(response)
            } catch (ex: Exception) {
                Result.Error(ex.message)
            }
        }
    }

    override suspend fun getRepresentatives(address: String): Result<List<Representative>> {
        return withContext(dispatcher) {
            try {
                val response = service.getRepresentatives(
                    address
                )
                val representative = response.offices.map { office ->
                    office.getRepresentatives(response.officials)
                }.flatten()
                Result.Success(representative)
            } catch (ex: Exception) {
                Result.Error(ex.message)
            }
        }
    }
}
