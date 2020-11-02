package com.silviucanton.myandroidapp.bugs.edit

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
import com.silviucanton.myandroidapp.bugs.data.Bug
import kotlinx.android.synthetic.main.fragment_bug_edit.*
import java.time.LocalDate
import java.util.*

class BugEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: BugEditViewModel
    private var itemId: Long? = null
    private var bug: Bug? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(javaClass.name, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                itemId = it.getLong(ITEM_ID)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(javaClass.name, "onCreateView")
        return inflater.inflate(R.layout.fragment_bug_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(javaClass.name, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(javaClass.name, "save bug")
            val i = bug
            if (i != null) {
                i.title = title_input.text.toString()
                i.description = description_input.text.toString()
                i.severity = severity_input.text.toString().toInt()
                i.solved = solved_checkbox.isChecked
                i.dateReported = reportedDate_input.text.toString()
                viewModel.saveOrUpdate(i)
            }
        }
        delete_button.setOnClickListener {
            Log.v(javaClass.name, "delete bug")
            val i = bug
            if (i != null) {
                viewModel.delete(i)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(BugEditViewModel::class.java)

        viewModel.fetching.observe(viewLifecycleOwner) { fetching ->
            Log.v(javaClass.name, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        }

        viewModel.fetchingError.observe(viewLifecycleOwner) { exception ->
            if(exception != null) {
                Log.v(javaClass.name, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.completed.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Log.v(javaClass.name, "completed, navigate back")
                findNavController().popBackStack()
            }
        }

        val id = itemId

        if (id == null) {
            bug = Bug(0, "FirstBug", "Bug description",  Date().toString(), 2, false)
        } else {
            viewModel.getItemById(id).observe(viewLifecycleOwner) {
                Log.v(javaClass.name, "update bug")
                bug = it
                title_input.setText(it.title)
                description_input.setText(it.description)
                severity_input.setText(it.severity.toString())
                reportedDate_input.setText(it.dateReported);
                solved_checkbox.isChecked = it.solved
            }
        }
    }
}