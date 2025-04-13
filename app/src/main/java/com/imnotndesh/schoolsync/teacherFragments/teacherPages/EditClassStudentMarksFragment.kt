package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class EditClassStudentMarksFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var studentRecyclerView: RecyclerView
    private val studentList = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    data class Student(
        val studentName: String,
        val className: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_class_student_marks, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        val title = view.findViewById<TextView>(R.id.titleText)
        val searchInput = view.findViewById<EditText>(R.id.searchStudentName)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        studentRecyclerView = view.findViewById(R.id.studentRecyclerView)

        title.text = "Search for student"

        adapter = StudentAdapter()
        studentRecyclerView.layoutManager = LinearLayoutManager(context)
        studentRecyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val queryName = searchInput.text.toString().trim()
            if (queryName.isNotEmpty()) {
                searchStudents(queryName)
            } else {
                Toast.makeText(context, "Enter student name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchStudents(partialName: String) {
        studentList.clear()
        val cursor: Cursor = dbHelper.findStudentsByStudentNameLike(partialName)
        if (cursor.moveToFirst()) {
            do {
                val studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"))
                val className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
                studentList.add(Student(studentName, className))
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(context, "No students found", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    inner class StudentAdapter : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

        inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val studentName: TextView = itemView.findViewById(R.id.studentName)
            val className: TextView = itemView.findViewById(R.id.className)
            val editButton: Button = itemView.findViewById(R.id.editButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_teacher_search_student_card, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = studentList[position]
            val itemStudentName = "Name: ${student.studentName}"
            val itemClassName = "Class: ${student.className}"
            holder.studentName.text = itemStudentName
            holder.className.text = itemClassName

            holder.editButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("studentName", student.studentName)
                }
                val fragment = EditStudentExamDetailsFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.teacher_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        override fun getItemCount(): Int = studentList.size
    }
}