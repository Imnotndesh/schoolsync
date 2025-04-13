package com.example.school_management_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import com.imnotndesh.schoolsync.R

class EditTeacherFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_edit_teacher, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        val searchInput = view.findViewById<EditText>(R.id.inputSearchTeacherName)
        val searchButton = view.findViewById<Button>(R.id.btnSearchTeacher)

        val newDetailsTitle = view.findViewById<TextView>(R.id.textNewDetailsTitle)
        val usernameInput = view.findViewById<EditText>(R.id.editInputUsername)
        val passwordInput = view.findViewById<EditText>(R.id.editInputPassword)
        val classNameInput = view.findViewById<EditText>(R.id.editInputClassName)
        val subjectInput = view.findViewById<EditText>(R.id.editInputSubject)
        val emailInput = view.findViewById<EditText>(R.id.editInputEmail)
        val phoneInput = view.findViewById<EditText>(R.id.editInputPhone)
        val updateButton = view.findViewById<Button>(R.id.btnSubmitEditTeacher)
        var keyTeacherName :String = ""
        searchButton.setOnClickListener {
            val searchName = searchInput.text.toString()
            keyTeacherName = searchName
            if (searchName.isBlank()) {
                Toast.makeText(requireContext(), "Enter teacher name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cursor = dbHelper.getTeacherByTeacherName(searchName)

            if (cursor.moveToFirst()) {
                // Show hidden fields
                newDetailsTitle.visibility = View.VISIBLE
                usernameInput.visibility = View.VISIBLE
                passwordInput.visibility = View.VISIBLE
                classNameInput.visibility = View.VISIBLE
                subjectInput.visibility = View.VISIBLE
                emailInput.visibility = View.VISIBLE
                phoneInput.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE

                // Prefill existing data
                usernameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")))
                passwordInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")))
                classNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("class_name")))
                subjectInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("subject")))
                emailInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")))
                phoneInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")))
                Toast.makeText(requireContext(), "$searchName found. Edit details below.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "Teacher not found", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
        }

        updateButton.setOnClickListener {
            val newSubject = subjectInput.text.toString()
            val newEmail = emailInput.text.toString()
            val newPhone = phoneInput.text.toString()
            val newClass = classNameInput.text.toString()
            val newPassword = passwordInput.text.toString()
            val newUsername = usernameInput.text.toString()
            if (newSubject.isBlank() || newEmail.isBlank() || newPhone.isBlank() || newClass.isBlank() || newPassword.isBlank() || newUsername.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val success = dbHelper.editTeacherByTeacherName(keyTeacherName,newSubject,newEmail,newPhone, newClass,newPassword, newUsername)
            if (success) {
                Toast.makeText(requireContext(), "$keyTeacherName updated successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Go back
            } else {
                Toast.makeText(requireContext(), "Failed to update $keyTeacherName",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
