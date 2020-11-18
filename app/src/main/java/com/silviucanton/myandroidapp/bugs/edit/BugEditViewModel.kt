package com.silviucanton.myandroidapp.bugs.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silviucanton.myandroidapp.bugs.data.Bug
import com.silviucanton.myandroidapp.bugs.data.BugRepository
import com.silviucanton.myandroidapp.bugs.data.local.BugDatabase
import com.silviucanton.myandroidapp.core.TAG
import kotlinx.coroutines.launch
import com.silviucanton.myandroidapp.core.Result

class BugEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    private val bugRepository: BugRepository


    init {
        val bugDao = BugDatabase.getDatabase(application, viewModelScope).bugDao()
        bugRepository = BugRepository(bugDao)
    }

    fun getItemById(itemId: Long): LiveData<Bug> {
        Log.v(TAG, "getItemById...")
        return bugRepository.getById(itemId)
    }

    fun saveOrUpdate(item: Bug) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...")
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Bug>
            if (item.id != 0L) {
                result = bugRepository.update(item)
            } else {
                result = bugRepository.save(item)
            }
            when(result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded")
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun delete(bug: Bug) {
        viewModelScope.launch {
            Log.v(TAG, "delete...");
            mutableFetching.value = true
            mutableException.value = null

            val result: Result<Bug>
            result = bugRepository.delete(bug)

            when(result) {
                is Result.Success -> {
                    Log.d(TAG, "delete succeeded")
                }
                is Result.Error -> {
                    Log.w(TAG, "delete failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}