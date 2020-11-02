package com.silviucanton.myandroidapp.bugs.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.silviucanton.myandroidapp.R
import com.silviucanton.myandroidapp.bugs.data.WebSocketEvent
import com.silviucanton.myandroidapp.bugs.data.remote.BugApi
import kotlinx.android.synthetic.main.fragment_bug_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class BugListFragment : Fragment() {

    private lateinit var bugViewModel: BugListViewModel
    private lateinit var bugListAdapter: MyBugListRecyclerViewAdapter
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(javaClass.name, "onCreate");
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bug_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(javaClass.name, "onActivityCreated")
//        if (!AuthRepository.isLoggedIn) {
//            findNavController().navigate(R.id.fragment_login)
//            return;
//        }
        setupBugList()
        fab.setOnClickListener {
            Log.v(javaClass.name, "navigate to add new guitar")
            findNavController().navigate(R.id.fragment_bug_edit)
        }
    }

    override fun onStart() {
        super.onStart()
        isListening = true
        CoroutineScope(Dispatchers.Main).launch { collectWebSocketEvents() }
    }

    override fun onStop() {
        super.onStop()
        isListening = false
    }

    private suspend fun collectWebSocketEvents() {
        while (isListening) {
            val event = Gson().fromJson<WebSocketEvent>(BugApi.eventChannel.receive(), WebSocketEvent::class.java)
            Log.i("MainActivity", "received $event")
            if (event.type.equals("created")) {
                bugViewModel.bugRepository.save(event.payload.bug,false)
            } else if(event.type.equals("updated")) {
                bugViewModel.bugRepository.update(event.payload.bug,false)
            } else if(event.type.equals("deleted")) {
                bugViewModel.bugRepository.delete(event.payload.bug,false)
            }
        }
    }

    private fun setupBugList() {
        bugListAdapter = MyBugListRecyclerViewAdapter(this)
        bug_list.adapter = bugListAdapter

        bugViewModel = ViewModelProvider(this).get(BugListViewModel::class.java)
        bugViewModel.items.observe(viewLifecycleOwner) { items ->
            Log.v(javaClass.name, "update items")
            bugListAdapter.items = items
        }

        bugViewModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(javaClass.name, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        bugViewModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(javaClass.name, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        bugViewModel.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(javaClass.name, "onDestroy")
    }
}