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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class DeleteTeacherFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var teacherRecyclerView: RecyclerView
    private val teacherList = mutableListOf<Teacher>()
    private lateinit var adapter: TeacherAdapter

    data class Teacher(
        val teacherName: String,
        val email: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delete_teacher, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        val teacherName = view.findViewById<EditText>(R.id.searchTeacherName)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        teacherRecyclerView = view.findViewById(R.id.teacherRecyclerView)

        adapter = TeacherAdapter()
        teacherRecyclerView.layoutManager = LinearLayoutManager(context)
        teacherRecyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val searchTeacherName = teacherName.text.toString().trim()
            if (searchTeacherName.isNotEmpty()) {
                searchTeachers(searchTeacherName)
            } else {
                Toast.makeText(context, "Enter username to search", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchTeachers(teacherName: String) {
        teacherList.clear()
        val cursor: Cursor = dbHelper.getTeacherByTeacherName(teacherName)
        if (cursor.moveToFirst()) {
            val storedTeacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
            val storedTeacherEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            teacherList.add(Teacher(storedTeacherName, storedTeacherEmail))
        } else {
            Toast.makeText(context, "No teacher found", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    inner class TeacherAdapter :
        RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

        inner class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val teacherName: TextView = itemView.findViewById(R.id.teacherName)
            val teacherEmail: TextView = itemView.findViewById(R.id.teacherEmail)
            val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_teacher_card, parent, false)
            return TeacherViewHolder(view)
        }

        override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
            val teacher = teacherList[position]
            val currTeacherEmail : String= "Name: ${teacher.teacherName}"
            val currTeacherName : String= "Email: ${teacher.email}"
            holder.teacherName.text = currTeacherName
            holder.teacherEmail.text = currTeacherEmail

            holder.deleteButton.setOnClickListener {
                val deleted = dbHelper.deleteTeacherByUsername(teacher.teacherName)
                if (deleted) {
                    teacherList.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        context,
                        "${teacher.teacherName} deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int = teacherList.size
    }
}