package com.example.android.politicalpreparedness.domain.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.domain.model.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ElectionDataSource {
    //Get all elections
    fun getElections(): LiveData<List<Election>>

    //Save election
    suspend fun saveElection(election: Election)

    //Get election by id
    suspend fun getElectionById(id: Int): Election?

    //Delete saved election
    suspend fun deleteElectionById(id: Int)

    //Clear elections
    suspend fun clearElections()
}

class ElectionLocalRepository(
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override fun getElections(): LiveData<List<Election>> {
        return electionDao.getAllElections()
    }

    override suspend fun saveElection(election: Election) {
        return withContext(ioDispatcher) {
            electionDao.insertElection(election)
        }
    }

    override suspend fun getElectionById(id: Int): Election? {
        return withContext(ioDispatcher) {
            electionDao.getElectionById(id)
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