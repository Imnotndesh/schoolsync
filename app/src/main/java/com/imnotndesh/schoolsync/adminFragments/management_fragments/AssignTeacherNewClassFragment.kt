package com.imnotndesh.schoolsync.adminFragments.management_fragments

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class AssignTeacherNewClassFragment : Fragment() {
    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var teacherNameEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var changeClassLayout: LinearLayout
    private lateinit var newClassNameEditText: EditText
    private lateinit var changeClassButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_assign_teacher_new_class, container, false)
        dbHelper = SchoolDbHelper(requireContext())

        teacherNameEditText = view.findViewById(R.id.teacherNameEditText)
        searchButton = view.findViewById(R.id.searchTeacherButton)
        changeClassLayout = view.findViewById(R.id.changeClassLayout)
        newClassNameEditText = view.findViewById(R.id.newClassNameEditText)
        changeClassButton = view.findViewById(R.id.changeClassButton)

        searchButton.setOnClickListener {
            val teacherName = teacherNameEditText.text.toString().trim()
            if (teacherName.isNotEmpty()) {
                val cursor: Cursor = dbHelper.getTeacherByTeacherName(teacherName)
                if (cursor.moveToFirst()) {
                    changeClassLayout.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Teacher not found", Toast.LENGTH_SHORT).show()
                    changeClassLayout.visibility = View.GONE
                }
                cursor.close()
            } else {
                Toast.makeText(requireContext(), "Enter a teacher name", Toast.LENGTH_SHORT).show()
            }
        }

        changeClassButton.setOnClickListener {
            val teacherName = teacherNameEditText.text.toString().trim()
            val newClassName = newClassNameEditText.text.toString().trim()
            if (newClassName.isNotEmpty()) {
                val result = dbHelper.changeTeacherClassByTeacherName(teacherName, newClassName)
                if (result) {
                    Toast.makeText(requireContext(), "Class updated", Toast.LENGTH_SHORT).show()
                    teacherNameEditText.text.clear()
                    newClassNameEditText.text.clear()
                    changeClassLayout.visibility = View.GONE
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter new class name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}