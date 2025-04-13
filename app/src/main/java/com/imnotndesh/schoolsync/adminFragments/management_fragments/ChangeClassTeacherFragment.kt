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

class ChangeClassTeacherFragment : Fragment() {
    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var classNameEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var changeTeacherLayout: LinearLayout
    private lateinit var newTeacherNameEditText: EditText
    private lateinit var changeTeacherButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_class_teacher, container, false)
        dbHelper = SchoolDbHelper(requireContext())

        classNameEditText = view.findViewById(R.id.classNameEditText)
        searchButton = view.findViewById(R.id.searchClassButton)
        changeTeacherLayout = view.findViewById(R.id.changeTeacherLayout)
        newTeacherNameEditText = view.findViewById(R.id.newTeacherNameEditText)
        changeTeacherButton = view.findViewById(R.id.changeTeacherButton)

        searchButton.setOnClickListener {
            val className = classNameEditText.text.toString().trim()
            if (className.isNotEmpty()) {
                val cursor: Cursor = dbHelper.getClassByClassName(className)
                if (cursor.moveToFirst()) {
                    changeTeacherLayout.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Class not found", Toast.LENGTH_SHORT).show()
                    changeTeacherLayout.visibility = View.GONE
                }
                cursor.close()
            } else {
                Toast.makeText(requireContext(), "Enter a class name", Toast.LENGTH_SHORT).show()
            }
        }

        changeTeacherButton.setOnClickListener {
            val className = classNameEditText.text.toString().trim()
            val newTeacherName = newTeacherNameEditText.text.toString().trim()
            if (newTeacherName.isNotEmpty()) {
                val result = dbHelper.changeClassTeacherByClassName(className, newTeacherName)
                if (result) {
                    Toast.makeText(requireContext(), "Teacher updated", Toast.LENGTH_SHORT).show()
                    classNameEditText.text.clear()
                    newTeacherNameEditText.text.clear()
                    changeTeacherLayout.visibility = View.GONE
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter new teacher name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}