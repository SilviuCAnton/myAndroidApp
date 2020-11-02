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
import kotlinx.coroutines.launch

class BugListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Bug>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val bugRepository: BugRepository

    init {
        val bugDao = BugDatabase.getDatabase(application, viewModelScope).bugDao()
        bugRepository = BugRepository(bugDao)
        items = bugRepository.bugs
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(javaClass.name, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            try {
                bugRepository.refresh()
            } catch(e: Exception) {
                Log.e(javaClass.name,e.toString())
                mutableException.value = e
            }
            mutableLoading.value = false
        }
    }
}