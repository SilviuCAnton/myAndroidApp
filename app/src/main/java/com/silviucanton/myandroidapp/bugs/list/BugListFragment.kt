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
import com.silviucanton.myandroidapp.R
import com.silviucanton.myandroidapp.auth.data.AuthRepository
import com.silviucanton.myandroidapp.core.TAG
import kotlinx.android.synthetic.main.fragment_bug_list.*

/**
 * A fragment representing a list of Items.
 */
class BugListFragment : Fragment() {

    private lateinit var bugViewModel: BugListViewModel
    private lateinit var bugListAdapter: MyBugListRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate");
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
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.fragment_login)
            return
        }
        setupBugList()
        fab.setOnClickListener {
            Log.v(javaClass.name, "navigate to add new guitar")
            findNavController().navigate(R.id.fragment_bug_edit)
        }
        logout.setOnClickListener{
            Log.v(TAG, "logging out")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
        }
    }

    private fun setupBugList() {
        bugListAdapter = MyBugListRecyclerViewAdapter(this)
        bug_list.adapter = bugListAdapter

        bugViewModel = ViewModelProvider(this).get(BugListViewModel::class.java)
        bugViewModel.items.observe(viewLifecycleOwner) { items ->
            Log.v(TAG, "update items")
            bugListAdapter.items = items
        }

        bugViewModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        bugViewModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
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