package com.imnotndesh.schoolsync.teacherFragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class StudentEnrollmentFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    private lateinit var studentNameEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var phoneEditText: EditText
    private lateinit var classNameEditText: EditText

    private lateinit var parentNameEditText: EditText
    private lateinit var parentEmailEditText: EditText
    private lateinit var parentPhoneEditText: EditText
    private lateinit var sharedPrefs:SharedPreferences

    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_enrollment, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPrefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val teacherUsername = sharedPrefs.getString("username", null)
        val className = dbHelper.getClassNameByTeacherUsername(teacherUsername!!)

        // Student fields
        studentNameEditText = view.findViewById(R.id.studentNameEditText)
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        genderSpinner = view.findViewById(R.id.genderSpinner)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        classNameEditText = view.findViewById(R.id.classNameEditText)
        classNameEditText.setText(className)

        // Parent fields
        parentNameEditText = view.findViewById(R.id.parentNameEditText)
        parentEmailEditText = view.findViewById(R.id.parentEmailEditText)
        parentPhoneEditText = view.findViewById(R.id.parentPhoneEditText)

        submitButton = view.findViewById(R.id.submitButton)

        // Gender spinner setup
        val genderOptions = arrayOf("Male", "Female", "Rather not say")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        submitButton.setOnClickListener {
            enrollStudentAndParent()
        }

        return view
    }

    private fun enrollStudentAndParent() {
        val studentName = studentNameEditText.text.toString().trim()
        val dateOfBirth = dateOfBirthEditText.text.toString().trim()
        val gender = genderSpinner.selectedItem.toString()
        val phone = phoneEditText.text.toString().trim()
        val className = classNameEditText.text.toString().trim()

        val parentName = parentNameEditText.text.toString().trim()
        val parentEmail = parentEmailEditText.text.toString().trim()
        val parentPhone = parentPhoneEditText.text.toString().trim()

        if (studentName.isEmpty() || dateOfBirth.isEmpty() || className.isEmpty() ||
            parentName.isEmpty() || parentEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val studentCreated = dbHelper.createStudent(
            studentName,
            dateOfBirth,
            parentName,  // shared parentName
            gender,
            if (phone.isEmpty()) null else phone,
            className
        )

        if (!studentCreated) {
            Toast.makeText(requireContext(), "Failed to enroll student", Toast.LENGTH_SHORT).show()
            return
        }

        val parentCreated = dbHelper.createParent(
            parentName,
            parentEmail,
            parentPhone,
            studentName
        )

        if (!parentCreated) {
            Toast.makeText(requireContext(), "Failed to save parent info", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Student enrolled successfully!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack() // Go back to previous fragment
    }
}
