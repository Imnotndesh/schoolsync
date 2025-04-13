package com.imnotndesh.schoolsync.adminPages.management_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class AddClassFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_class, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        // UI references
        val classNameEditText = view.findViewById<EditText>(R.id.classNameEditText)
        val classStreamEditText = view.findViewById<EditText>(R.id.classStreamEditText)
        val gradeEditText = view.findViewById<EditText>(R.id.gradeEditText)
        val teacherNameEditText = view.findViewById<EditText>(R.id.teacherNameEditText)
        val capacityEditText = view.findViewById<EditText>(R.id.capacityEditText)
        val addButton = view.findViewById<Button>(R.id.addClassButton)

        addButton.setOnClickListener {
            val className = classNameEditText.text.toString().trim()
            val classStream = classStreamEditText.text.toString().trim()
            val grade = gradeEditText.text.toString().trim()
            val teacherName = teacherNameEditText.text.toString().trim()
            val capacityText = capacityEditText.text.toString().trim()

            // Validate inputs
            if (className.isEmpty() || classStream.isEmpty() || grade.isEmpty() ||
                teacherName.isEmpty() || capacityText.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val capacity = capacityText.toIntOrNull()
            if (capacity == null || capacity <= 0) {
                Toast.makeText(requireContext(), "Enter a valid capacity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.createClass(className, classStream, grade, teacherName, capacity)
            if (success) {
                Toast.makeText(requireContext(), "Class added successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to add class", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}