package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import android.app.DatePickerDialog
import android.content.Context
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class EditAttendanceFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var selectDateButton: Button
    private lateinit var searchDateEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var attendanceFieldsLayout: LinearLayout
    private lateinit var presentEditText: EditText
    private lateinit var absentEditText: EditText
    private lateinit var studentsMissingEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var teacherUsername: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_edit_attendance, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        selectDateButton = binding.findViewById(R.id.selectDateButton)
        searchDateEditText = binding.findViewById(R.id.searchDateEditText)
        searchButton = binding.findViewById(R.id.searchButton)
        attendanceFieldsLayout = binding.findViewById(R.id.attendanceFieldsLayout)
        presentEditText = binding.findViewById(R.id.presentEditText)
        absentEditText = binding.findViewById(R.id.absentEditText)
        studentsMissingEditText = binding.findViewById(R.id.studentsMissingEditText)
        submitButton = binding.findViewById(R.id.submitButton)

        // Get teacher's username from SharedPreferences
        teacherUsername = getTeacherUsernameFromSharedPreferences()

        // Set today's date in the date picker field
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        searchDateEditText.setText(today)

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        searchButton.setOnClickListener {
            val selectedDate = searchDateEditText.text.toString()
            searchAttendanceByDate(selectedDate)
        }

        submitButton.setOnClickListener {
            val selectedDate = searchDateEditText.text.toString()
            val present = presentEditText.text.toString().toIntOrNull() ?: 0
            val absent = absentEditText.text.toString().toIntOrNull() ?: 0
            val studentsMissing = studentsMissingEditText.text.toString()
            submitAttendance(selectedDate, present, absent, studentsMissing)
        }

        return binding
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                searchDateEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun searchAttendanceByDate(date: String) {
        val isEntryExists = dbHelper.isAttendanceEntryExistsForDate(teacherUsername, date)
        if (isEntryExists) {
            // Fetch and populate data into the fields if entry exists
            val cursor = dbHelper.getAttendanceByUsernameAndDate(teacherUsername, date)
            if (cursor.moveToFirst()) {
                val presentNum = cursor.getInt(cursor.getColumnIndexOrThrow("present"))
                val studentsMissing = cursor.getString(cursor.getColumnIndexOrThrow("students_missing"))
                val absentNum = cursor.getInt(cursor.getColumnIndexOrThrow("absent"))
                presentEditText.setText(presentNum)
                absentEditText.setText(absentNum)
                studentsMissingEditText.setText(studentsMissing)
            }
            cursor.close()

            // Show the edit fields and submit button
            attendanceFieldsLayout.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE
        } else {
            // If no entry exists, show a Toast message
            Toast.makeText(requireContext(), "No entry found for the selected date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitAttendance(date: String, present: Int, absent: Int, studentsMissing: String) {
        val success = dbHelper.editAttendanceByTeacherUsername(
            teacherUsername,
            date,
            absent,
            present,
            studentsMissing
        )

        if (success) {
            Toast.makeText(requireContext(), "Attendance updated successfully", Toast.LENGTH_SHORT).show()
            // Navigate back to previous fragment
            requireActivity().onBackPressed()
        } else {
            Toast.makeText(requireContext(), "Failed to update attendance", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTeacherUsernameFromSharedPreferences(): String {
        val sharedPrefs = requireContext().getSharedPreferences("teacher_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("teacher_username", "") ?: ""
    }
}

