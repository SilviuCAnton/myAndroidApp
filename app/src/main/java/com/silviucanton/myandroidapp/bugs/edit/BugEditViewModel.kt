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
import kotlinx.coroutines.launch

class BugEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    private val bugRepository: BugRepository


    init {
        val bugDao = BugDatabase.getDatabase(application, viewModelScope).bugDao();
        bugRepository = BugRepository(bugDao)
    }

    fun getItemById(itemId: Long): LiveData<Bug> {
        Log.v(javaClass.name, "getItemById...")
        return bugRepository.getById(itemId)
    }

    fun saveOrUpdate(item: Bug) {
        viewModelScope.launch {
            Log.v(javaClass.name, "saveOrUpdateItem...")
            mutableFetching.value = true
            mutableException.value = null
            try {
                if (item.id != 0L) {
                    bugRepository.update(item, true)
                } else {
                    bugRepository.save(item, true)
                }
            }
            catch (e: Exception) {
                Log.w(javaClass.name, "saveOrUpdateItem failed", e)
                mutableException.value = e
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun delete(bug: Bug) {
        viewModelScope.launch {
            Log.v(javaClass.name, "delete...");
            mutableFetching.value = true
            mutableException.value = null
            try {
                bugRepository.delete(bug, true)
            }
            catch (e: Exception) {
                Log.w(javaClass.name, "delete failed", e)
                mutableException.value = e
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}