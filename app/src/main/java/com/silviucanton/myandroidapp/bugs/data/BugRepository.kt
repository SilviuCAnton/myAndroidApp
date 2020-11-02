package com.silviucanton.myandroidapp.bugs.data

import androidx.lifecycle.LiveData
import com.silviucanton.myandroidapp.bugs.data.local.BugDao
import com.silviucanton.myandroidapp.bugs.data.remote.BugApi
import com.silviucanton.myandroidapp.core.Result

class BugRepository(private val bugDao: BugDao) {

    val bugs = bugDao.getAll()

    suspend fun refresh(): Result<Boolean> {
        try {
            val bugs = BugApi.service.find()
            for (bug in bugs) {
                bugDao.insert(bug)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(bugId: Long): LiveData<Bug> {
        return bugDao.getById(bugId)
    }

    suspend fun save(bug: Bug, connected: Boolean): Result<Bug> {
        try {
            return if(connected) {
                val createdBug = BugApi.service.create(bug)
//                bugDao.insert(bug)
                Result.Success(createdBug)
            } else {
                bugDao.insert(bug)
                Result.Success(bug)
            }
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(bug: Bug, connected: Boolean): Bug {
        if (connected){
            BugApi.service.delete(bug.id)
            bugDao.delete(bug)
        } else {
            bugDao.delete(bug)
        }
        return bug
    }

    suspend fun update(bug: Bug, connected: Boolean): Result<Bug> {
        try {
            return if(connected) {
                val updatedBug = BugApi.service.update(bug.id, bug)
//                bugDao.update(updatedBug)
                Result.Success(updatedBug)
            } else {
                bugDao.update(bug)
                Result.Success(bug)
            }

        } catch(e: Exception) {
            return Result.Error(e)
        }
    }
}