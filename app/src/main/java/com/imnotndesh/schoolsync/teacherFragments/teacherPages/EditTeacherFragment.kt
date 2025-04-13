package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.SharedPreferences
import com.imnotndesh.schoolsync.MainActivity
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class EditTeacherFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHelper = SchoolDbHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        val nameField = view.findViewById<EditText>(R.id.editTeacherName)
        val subjectField = view.findViewById<EditText>(R.id.editTeacherSubject)
        val emailField = view.findViewById<EditText>(R.id.editTeacherEmail)
        val phoneField = view.findViewById<EditText>(R.id.editTeacherPhone)
        val classNameField = view.findViewById<EditText>(R.id.editTeacherClass)
        val passwordField = view.findViewById<EditText>(R.id.editTeacherPassword)
        val updateButton = view.findViewById<Button>(R.id.buttonUpdateTeacher)
        val deleteButton = view.findViewById<Button>(R.id.buttonDeleteTeacher)

        val cursor = dbHelper.getTeachersByUsername(username!!)
        if (cursor.moveToFirst()) {
            nameField.setText(cursor.getString(cursor.getColumnIndexOrThrow("teacher_name")))
            subjectField.setText(cursor.getString(cursor.getColumnIndexOrThrow("subject")))
            emailField.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")))
            phoneField.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")))
            classNameField.setText(cursor.getString(cursor.getColumnIndexOrThrow("class_name")))
            passwordField.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")))
        }
        cursor.close()

        updateButton.setOnClickListener {
            val updated = dbHelper.editTeacherByUsername(
                username,
                nameField.text.toString(),
                subjectField.text.toString(),
                emailField.text.toString(),
                phoneField.text.toString(),
                classNameField.text.toString(),
                passwordField.text.toString()
            )

            if (updated) {
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            val deleted = dbHelper.deleteTeacherByUsername(username)
            if (deleted) {
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

