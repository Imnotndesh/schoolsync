package com.imnotndesh.schoolsync.adminFragments.management_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import com.imnotndesh.schoolsync.R

class AddTeacherFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_teacher, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        val teacherNameInput = view.findViewById<EditText>(R.id.inputTeacherName)
        val usernameInput = view.findViewById<EditText>(R.id.inputUsername)
        val passwordInput = view.findViewById<EditText>(R.id.inputPassword)
        val classNameInput = view.findViewById<EditText>(R.id.inputClassName)
        val subjectInput = view.findViewById<EditText>(R.id.inputSubject)
        val emailInput = view.findViewById<EditText>(R.id.inputEmail)
        val phoneInput = view.findViewById<EditText>(R.id.inputPhone)
        val submitButton = view.findViewById<Button>(R.id.btnSubmitTeacher)

        submitButton.setOnClickListener {
            val teacherName = teacherNameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val className = classNameInput.text.toString()
            val subject = subjectInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()

            if (teacherName.isBlank() || username.isBlank() || password.isBlank() ||
                className.isBlank() || subject.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                val success = dbHelper.createTeacher(
                    teacherName, username, password, className, subject, email, phone
                )

                if (success) {
                    Toast.makeText(requireContext(), "$teacherName added successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // Go back
                } else {
                    Toast.makeText(requireContext(), "Failed to add $teacherName", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
