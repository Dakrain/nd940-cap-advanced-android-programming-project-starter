package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ElectionDataSource {
    //Get all elections
    suspend fun getElections(): Result<List<Election>>

    //Save election
    suspend fun saveElection(election: Election)

    //Get election by id
    suspend fun getElectionById(id: Int): Result<Election>

    //Delete saved election
    suspend fun deleteElectionById(id: Int)

    //Clear elections
    suspend fun clearElections()
}

class ElectionLocalRepository(
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(electionDao.getAllElections())
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }
    }

    override suspend fun saveElection(election: Election) {
        withContext(ioDispatcher) {
            electionDao.insertElection(election)
        }
    }

    override suspend fun getElectionById(id: Int): Result<Election> {
        return withContext(ioDispatcher) {
            try {
                val result = electionDao.getElectionById(id)
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error("Election not found!")
                }

            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }
    }

    override suspend fun deleteElectionById(id: Int) {
        withContext(ioDispatcher) {
            electionDao.deleteElectionById(id)
        }
    }

    override suspend fun clearElections() {
        withContext(ioDispatcher) {
            electionDao.clearElections()
        }
    }

}