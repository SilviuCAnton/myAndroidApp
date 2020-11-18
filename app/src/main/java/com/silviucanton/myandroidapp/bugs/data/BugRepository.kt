package com.silviucanton.myandroidapp.bugs.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.silviucanton.myandroidapp.bugs.data.local.BugDao
import com.silviucanton.myandroidapp.bugs.data.remote.BugApi
import com.silviucanton.myandroidapp.bugs.data.remote.MessageData
import com.silviucanton.myandroidapp.core.Api
import com.silviucanton.myandroidapp.core.Constants
import com.silviucanton.myandroidapp.core.Result
import com.silviucanton.myandroidapp.core.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BugRepository(private val bugDao: BugDao) {

    val bugs = MediatorLiveData<List<Bug>>().apply { postValue(emptyList()) }

    init {
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    private suspend fun collectEvents() {
        while (true) {
            val messageData = Gson().fromJson(Api.eventChannel.receive(), MessageData::class.java)
            Log.d("GLF: collectEvents", "received $messageData")
            handleMessage(messageData)
        }
    }

    private suspend fun handleMessage(messageData: MessageData) {
        val bug = messageData.payload.bug
        when (messageData.event) {
            "created" -> bugDao.insert(bug)
            "updated" -> bugDao.update(bug)
            "deleted" -> bugDao.delete(bug)
            else -> {
                Log.d("GLF: handleMessage", "received $messageData")
            }
        }
    }

    suspend fun refresh(): Result<Boolean> {
        try {
            val obtainedBugs = BugApi.service.find()
            bugs.value = obtainedBugs
            for (bug in obtainedBugs) {
                bugDao.insert(bug)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            Log.v(TAG, "Suntem in offline!")
            val userId = Constants.instance()?.fetchValueString("_id")
            Log.v(TAG, "Avem ID $userId")
            bugs.addSource(bugDao.getAll(userId!!)){
                bugs.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(bugId: Long): LiveData<Bug> {
        return bugDao.getById(bugId)
    }

    suspend fun save(bug: Bug): Result<Bug> {
        try {
            val createdItem = BugApi.service.create(bug)
            bugDao.insert(createdItem)
            return Result.Success(createdItem)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(bug: Bug): Result<Bug> {
        try {
            BugApi.service.delete(bug.id)
            bugDao.delete(bug)
            return Result.Success(bug)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(bug: Bug): Result<Bug> {
        try {
            val updatedBug = BugApi.service.update(bug.id, bug)
            bugDao.update(updatedBug)
            return Result.Success(updatedBug)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }
}