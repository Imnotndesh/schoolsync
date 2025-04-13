package com.imnotndesh.schoolsync.adminPages.management_fragments

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class RemoveStudentFragment : Fragment() {
    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private val studentList = mutableListOf<StudentItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_remove_student, container, false)
        dbHelper = SchoolDbHelper(requireContext())

        val searchEditText = view.findViewById<EditText>(R.id.searchStudentName)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        recyclerView = view.findViewById(R.id.studentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentAdapter(studentList, dbHelper)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val studentName = searchEditText.text.toString()
            if (studentName.isNotEmpty()) {
                searchStudent(studentName)
            } else {
                Toast.makeText(requireContext(), "Enter student name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchStudent(studentName: String) {
        val cursor: Cursor = dbHelper.getStudentByName(studentName)
        studentList.clear()

        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("student_name"))
            val studentClass = cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
            studentList.add(StudentItem(name, studentClass))
        } else {
            Toast.makeText(requireContext(), "No student found", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        adapter.notifyDataSetChanged()
    }

    data class StudentItem(val studentName: String, val studentClass: String)

   inner class StudentAdapter(private val students: MutableList<StudentItem>, private val dbHelper: SchoolDbHelper) :
        RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

        inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val studentNameText: TextView = view.findViewById(R.id.studentNameText)
            val studentClassText: TextView = view.findViewById(R.id.studentClassText)
            val deleteButton: Button = view.findViewById(R.id.deleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_card, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = students[position]
            val itemName = "Name: ${student.studentName}"
            val itemClass = "Class: ${student.studentClass}"
            holder.studentNameText.text = itemName
            holder.studentClassText.text = itemClass

            holder.deleteButton.setOnClickListener {
                val result = dbHelper.deleteStudentByName(student.studentName)
                if (result) {
                    students.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        holder.itemView.context,
                        "Deleted ${student.studentName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed to delete ${student.studentName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        override fun getItemCount() = students.size
    }
}