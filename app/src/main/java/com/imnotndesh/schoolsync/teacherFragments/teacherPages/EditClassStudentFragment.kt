package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class EditClassStudentFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedprefs:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_class_student, container, false)
        sharedprefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val teacherUsername = sharedprefs.getString("username", null)
        dbHelper = SchoolDbHelper(requireContext())

        val searchInput = view.findViewById<EditText>(R.id.inputSearchStudentName)
        val searchButton = view.findViewById<Button>(R.id.btnSearchStudent)

        val newDetailsTitle = view.findViewById<TextView>(R.id.textNewStudentDetailsTitle)
        val dobInput = view.findViewById<EditText>(R.id.editInputDateOfBirth)
        val genderInput = view.findViewById<EditText>(R.id.editInputGender)
        val phoneInput = view.findViewById<EditText>(R.id.editInputPhone)
        val classNameInput = view.findViewById<EditText>(R.id.editInputClassName)
        val updateButton = view.findViewById<Button>(R.id.btnSubmitEditStudent)

        searchButton.setOnClickListener {
            val searchName = searchInput.text.toString()
            if (searchName.isBlank()) {
                Toast.makeText(requireContext(), "Enter student name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val isStudentInTeacherClass = dbHelper.isStudentInTeacherClass(teacherUsername!!,searchName)
            if (!isStudentInTeacherClass) {
                Toast.makeText(requireContext(), "Student not found in your class", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cursor = dbHelper.getStudentByName(searchName)

            if (cursor.moveToFirst()) {
                // Show hidden fields
                newDetailsTitle.visibility = View.VISIBLE
                dobInput.visibility = View.VISIBLE
                genderInput.visibility = View.VISIBLE
                phoneInput.visibility = View.VISIBLE
                classNameInput.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE

                // Prefill existing data
                dobInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")))
                genderInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("gender")))
                phoneInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")))
                classNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("class_name")))

                Toast.makeText(requireContext(), "$searchName found. Edit details below.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "Student not found", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
        }

        updateButton.setOnClickListener {
            val searchName = searchInput.text.toString()
            val newDob = dobInput.text.toString()
            val newGender = genderInput.text.toString()
            val newPhone = phoneInput.text.toString()
            val newClass = classNameInput.text.toString()
            if (newDob.isBlank() || newGender.isBlank() || newClass.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val res = dbHelper.editStudentByName(searchName, newDob, newGender, newPhone, newClass)
            if (res) {
                Toast.makeText(requireContext(), "$searchName updated successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to update $searchName", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}