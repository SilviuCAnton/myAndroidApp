package com.silviucanton.myandroidapp.bugs.list

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.silviucanton.myandroidapp.R
import com.silviucanton.myandroidapp.bugs.data.Bug
import com.silviucanton.myandroidapp.bugs.edit.BugEditFragment
import com.silviucanton.myandroidapp.core.TAG
import kotlinx.android.synthetic.main.fragment_bug.view.*


class MyBugListRecyclerViewAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<MyBugListRecyclerViewAdapter.ViewHolder>() {

    var items = emptyList<Bug>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onItemClick: View.OnClickListener

    init {
        onItemClick = View.OnClickListener { view ->
            val item = view.tag as Bug
            fragment.findNavController().navigate(R.id.fragment_bug_edit, Bundle().apply {
                putLong(BugEditFragment.ITEM_ID, item.id)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_bug, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val item = items[position]
        holder.itemView.tag = item
        holder.textView.text = item.toString()
        holder.itemView.setOnClickListener(onItemClick)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.text
    }
}