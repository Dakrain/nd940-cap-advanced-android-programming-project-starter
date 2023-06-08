package com.example.android.politicalpreparedness.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.domain.model.Election

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(electionDTO: Election)

    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getElectionById(id: Int): Election?

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteElectionById(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clearElections()
}