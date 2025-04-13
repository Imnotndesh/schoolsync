package com.imnotndesh.schoolsync.adminFragments.management_fragments

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class RemoveClassFragment : Fragment() {
    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassAdapter
    private val classList = mutableListOf<ClassItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_remove_class, container, false)
        dbHelper = SchoolDbHelper(requireContext())

        val searchEditText = view.findViewById<EditText>(R.id.searchClassName)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        recyclerView = view.findViewById(R.id.classRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ClassAdapter(classList, dbHelper)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val className = searchEditText.text.toString()
            if (className.isNotEmpty()) {
                searchClass(className)
            } else {
                Toast.makeText(requireContext(), "Enter class name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchClass(className: String) {
        val cursor: Cursor = dbHelper.getClassByClassName(className)
        classList.clear()

        if (cursor.moveToFirst()) {
            val storedClassName = cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
            val stream = cursor.getString(cursor.getColumnIndexOrThrow("class_stream"))
            classList.add(ClassItem(storedClassName, stream))
        } else {
            Toast.makeText(requireContext(), "No class found", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        adapter.notifyDataSetChanged()
    }

    data class ClassItem(val className: String, val classStream: String)

    inner class ClassAdapter(private val classes: MutableList<ClassItem>, private val dbHelper: SchoolDbHelper) :
        RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {
        inner class ClassViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val classNameText: TextView = view.findViewById(R.id.classNameText)
            val classStreamText: TextView = view.findViewById(R.id.classStreamText)
            val deleteButton: Button = view.findViewById(R.id.deleteButton)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_class_card, parent, false)
            return ClassViewHolder(view)
        }
        override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
            val classItem = classes[position]
            val itemName = "Name: ${classItem.className}"
            val itemStream = "Stream: ${classItem.classStream}"
            holder.classNameText.text = itemName
            holder.classStreamText.text = itemStream

            holder.deleteButton.setOnClickListener {
                val result = dbHelper.deleteClassByClassName(classItem.className)
                if (result) {
                    classes.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        holder.itemView.context,
                        "Deleted ${classItem.className}",
                        Toast.LENGTH_SHORT
                    ).show()
                    backToPreviousFragment()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed to delete ${classItem.className}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        override fun getItemCount() = classes.size
    }
    private fun backToPreviousFragment(){
        parentFragmentManager.popBackStack()
    }
}