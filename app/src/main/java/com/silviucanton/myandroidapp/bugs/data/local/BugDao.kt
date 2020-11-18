package com.silviucanton.myandroidapp.bugs.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.silviucanton.myandroidapp.bugs.data.Bug

@Dao
interface BugDao {
    @Query("SELECT * from bugs WHERE userId=:userId ORDER BY title ASC")
    fun getAll(userId: String): LiveData<List<Bug>>

    @Query("SELECT * FROM bugs WHERE id=:id ")
    fun getById(id: Long): LiveData<Bug>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bug: Bug)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(bug: Bug)

    @Query("DELETE FROM bugs")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(bug: Bug)
}