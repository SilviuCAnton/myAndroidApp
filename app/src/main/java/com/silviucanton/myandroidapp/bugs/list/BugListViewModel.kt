package com.silviucanton.myandroidapp.bugs.list

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

class BugListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Bug>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    private val bugRepository: BugRepository

    init {
        val bugDao = BugDatabase.getDatabase(application, viewModelScope).bugDao()
        bugRepository = BugRepository(bugDao)
        items = bugRepository.bugs
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = bugRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}