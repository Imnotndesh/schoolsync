package com.imnotndesh.schoolsync.adminPages.management_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
class AddStudentFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_student, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        // UI references
        val studentNameEditText = view.findViewById<EditText>(R.id.studentNameEditText)
        val dateOfBirthEditText = view.findViewById<EditText>(R.id.dateOfBirthEditText)
        val parentNameEditText = view.findViewById<EditText>(R.id.parentNameEditText)
        val genderSpinner = view.findViewById<Spinner>(R.id.genderSpinner)
        val phoneEditText = view.findViewById<EditText>(R.id.phoneEditText)
        val classNameEditText = view.findViewById<EditText>(R.id.classNameEditText)
        val addButton = view.findViewById<Button>(R.id.addStudentButton)

        // Gender spinner options
        val genderOptions = arrayOf("Male", "Female", "Rather not say")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        addButton.setOnClickListener {
            val studentName = studentNameEditText.text.toString().trim()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val parentName = parentNameEditText.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()
            val phone = phoneEditText.text.toString().trim()
            val className = classNameEditText.text.toString().trim()

            // Validate required fields
            if (studentName.isEmpty() || dateOfBirth.isEmpty() || parentName.isEmpty() || className.isEmpty()|| phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.createStudent(
                studentName, dateOfBirth, parentName, gender,
                phone, className
            )

            if (success) {
                Toast.makeText(requireContext(), "Student added successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to add student", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}