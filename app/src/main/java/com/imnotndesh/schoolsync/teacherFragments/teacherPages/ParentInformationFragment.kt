package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class ParentInformationFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var studentNameEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var parentRecyclerView: RecyclerView
    private val parentList = mutableListOf<Parent>()
    private lateinit var adapter: ParentAdapter

    data class Parent(
        val parentName: String,
        val email: String,
        val phone: String,
        val studentName: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent_information, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        studentNameEditText = view.findViewById(R.id.searchStudentEditText)
        searchButton = view.findViewById(R.id.searchButton)
        parentRecyclerView = view.findViewById(R.id.parentRecyclerView)

        adapter = ParentAdapter()
        parentRecyclerView.layoutManager = LinearLayoutManager(context)
        parentRecyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val studentName = studentNameEditText.text.toString().trim()

            if (studentName.isEmpty()) {
                Toast.makeText(requireContext(), "Enter student name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val username = prefs.getString("username", null)

            if (username == null) {
                Toast.makeText(requireContext(), "Error fetching teacher info", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isInClass = dbHelper.isStudentInTeacherClass(username, studentName)

            if (!isInClass) {
                Toast.makeText(requireContext(), "Student not in your class", Toast.LENGTH_SHORT).show()
                parentList.clear()
                adapter.notifyDataSetChanged()
                return@setOnClickListener
            }
            fetchParentInfo(studentName)
        }
        return view
    }

    private fun fetchParentInfo(studentName: String) {
        parentList.clear()
        val cursor: Cursor = dbHelper.findParentInfoByStudentName(studentName)

        if (cursor.moveToFirst()) {
            do {
                val parentName = cursor.getString(cursor.getColumnIndexOrThrow("parent_name"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val student = cursor.getString(cursor.getColumnIndexOrThrow("student_name"))

                parentList.add(Parent(parentName, email, phone, student))

            } while (cursor.moveToNext())
        } else {
            Toast.makeText(requireContext(), "No parent information found", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    inner class ParentAdapter : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

        inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val parentNameText: TextView = itemView.findViewById(R.id.parentNameText)
            val emailText: TextView = itemView.findViewById(R.id.emailText)
            val phoneText: TextView = itemView.findViewById(R.id.phoneText)
            val studentNameText: TextView = itemView.findViewById(R.id.studentNameText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_parent_card, parent, false)
            return ParentViewHolder(view)
        }

        override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
            val parent = parentList[position]
            val itemParentName = "Parent Name: ${parent.parentName}"
            val itemParentEmail = "Email: ${parent.email}"
            val itemParentPhone = "Phone: ${parent.phone}"
            val itemParentStudentName = "Student: ${parent.studentName}"
            holder.parentNameText.text = itemParentName
            holder.emailText.text = itemParentEmail
            holder.phoneText.text = itemParentPhone
            holder.studentNameText.text = itemParentStudentName
        }

        override fun getItemCount(): Int = parentList.size
    }
}
