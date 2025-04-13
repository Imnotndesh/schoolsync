package com.imnotndesh.schoolsync.adminPages.management_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ChangeStudentClassFragment : Fragment() {

    private lateinit var studentNameEditText: EditText
    private lateinit var newClassNameEditText: EditText
    private lateinit var changeClassButton: Button
    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_student_class, container, false)

        studentNameEditText = view.findViewById(R.id.editTextStudentName)
        newClassNameEditText = view.findViewById(R.id.editTextNewClassName)
        changeClassButton = view.findViewById(R.id.buttonChangeStudentClass)

        dbHelper = SchoolDbHelper(requireContext())

        changeClassButton.setOnClickListener {
            val studentName = studentNameEditText.text.toString().trim()
            val newClassName = newClassNameEditText.text.toString().trim()

            if (studentName.isEmpty() || newClassName.isEmpty()) {
                Toast.makeText(requireContext(), "Fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val result = dbHelper.changeStudentClassByStudentName(studentName, newClassName)
                if (result) {
                    Toast.makeText(requireContext(), "Student class updated", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Update failed. Check student name.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
