package com.imnotndesh.schoolsync.adminPages.management_fragments

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class EditClassFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper

    private lateinit var inputSearchClassName: EditText
    private lateinit var btnSearchClass: Button

    private lateinit var textNewClassDetailsTitle: TextView
    private lateinit var editInputClassStream: EditText
    private lateinit var editInputGrade: EditText
    private lateinit var editInputTeacherName: EditText
    private lateinit var editInputCapacity: EditText
    private lateinit var btnSubmitEditClass: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_edit_class, container, false)

        dbHelper = SchoolDbHelper(requireContext())

        // Initialize views
        inputSearchClassName = view.findViewById(R.id.inputSearchClassName)
        btnSearchClass = view.findViewById(R.id.btnSearchClass)

        textNewClassDetailsTitle = view.findViewById(R.id.textNewClassDetailsTitle)
        editInputClassStream = view.findViewById(R.id.editInputClassStream)
        editInputGrade = view.findViewById(R.id.editInputGrade)
        editInputTeacherName = view.findViewById(R.id.editInputTeacherName)
        editInputCapacity = view.findViewById(R.id.editInputCapacity)
        btnSubmitEditClass = view.findViewById(R.id.btnSubmitEditClass)

        btnSearchClass.setOnClickListener {
            val className = inputSearchClassName.text.toString().trim()

            if (className.isEmpty()) {
                Toast.makeText(requireContext(), "Enter class name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cursor: Cursor = dbHelper.getClassByClassName(className)
            if (cursor.moveToFirst()) {
                // Show fields
                toggleNewDetailsVisibility()
                val savedClassCapacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity")).toString()
                val savedClassGrade = cursor.getInt(cursor.getColumnIndexOrThrow("grade")).toString()
                val savedClassStream = cursor.getString(cursor.getColumnIndexOrThrow("class_stream"))
                val savedTeacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
                // Prefill data
                editInputClassStream.setText(savedClassStream)
                editInputGrade.setText(savedClassGrade)
                editInputTeacherName.setText(savedTeacherName)
                editInputCapacity.setText(savedClassCapacity)

            } else {
                Toast.makeText(requireContext(), "Class not found", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        btnSubmitEditClass.setOnClickListener {
            val className = inputSearchClassName.text.toString().trim()
            val classStream = editInputClassStream.text.toString().trim()
            val grade = editInputGrade.text.toString().trim()
            val teacherName = editInputTeacherName.text.toString().trim()
            val capacityStr = editInputCapacity.text.toString().trim()

            if (classStream.isEmpty() || grade.isEmpty() || teacherName.isEmpty() || capacityStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val capacity = capacityStr.toInt()

            val success = dbHelper.editClassByClassName(
                className,
                classStream,
                grade,
                teacherName,
                capacity
            )

            if (success) {
                Toast.makeText(requireContext(), "Class updated successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()  // Go back
            } else {
                Toast.makeText(requireContext(), "Failed to update class", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun toggleNewDetailsVisibility() {
        val visibility = View.VISIBLE
        textNewClassDetailsTitle.visibility = visibility
        editInputClassStream.visibility = visibility
        editInputGrade.visibility = visibility
        editInputTeacherName.visibility = visibility
        editInputCapacity.visibility = visibility
        btnSubmitEditClass.visibility = visibility
    }
}
