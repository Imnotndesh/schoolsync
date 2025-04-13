package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TakeAttendanceFragment : Fragment() {

    private lateinit var presentEditText: EditText
    private lateinit var absentEditText: EditText
    private lateinit var studentsMissingEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var dateTakenTextView: TextView

    private lateinit var dbHelper: SchoolDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_take_attendance, container, false)

        // Initialize views
        presentEditText = view.findViewById(R.id.presentEditText)
        absentEditText = view.findViewById(R.id.absentEditText)
        studentsMissingEditText = view.findViewById(R.id.studentsMissingEditText)
        submitButton = view.findViewById(R.id.submitButton)
        dateTakenTextView = view.findViewById(R.id.dateTaken)

        dbHelper = SchoolDbHelper(requireContext())

        // Autofill today's date and class name
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currDateText = "Date: $currentDate"
        dateTakenTextView.text = currDateText

        val username = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("teacher_username", "") ?: ""

        // Set up the submit button
        submitButton.setOnClickListener {
            val present = presentEditText.text.toString().toIntOrNull() ?: 0
            val absent = absentEditText.text.toString().toIntOrNull() ?: 0
            val studentsMissing = studentsMissingEditText.text.toString()

            // Submit the attendance data
            val dateTaken = currentDate
            val result = dbHelper.takeAttendanceByTeacherUsername(username, dateTaken, absent, present, studentsMissing)

            if (result) {
                // If successful, go back to the previous fragment
                Toast.makeText(requireContext(), "Attendance Submitted Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to Submit Attendance", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
