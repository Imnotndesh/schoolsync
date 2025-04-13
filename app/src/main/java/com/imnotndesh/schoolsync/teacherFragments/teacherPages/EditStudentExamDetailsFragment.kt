package com.imnotndesh.schoolsync.teacherFragments.teacherPages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class EditStudentExamDetailsFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private var studentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SchoolDbHelper(requireContext())

        // Retrieve student name from bundle
        studentName = arguments?.getString("studentName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_student_exam_details, container, false)

        val studentNameText = view.findViewById<TextView>(R.id.studentNameText)
        val examDateInput = view.findViewById<EditText>(R.id.examDateInput)
        val examNameInput = view.findViewById<EditText>(R.id.examNameInput)
        val catOneInput = view.findViewById<EditText>(R.id.catOneInput)
        val catTwoInput = view.findViewById<EditText>(R.id.catTwoInput)
        val finalExamInput = view.findViewById<EditText>(R.id.finalExamInput)
        val commentsInput = view.findViewById<EditText>(R.id.commentsInput)
        val saveButton = view.findViewById<Button>(R.id.saveExamButton)

        val studentAutofill = "Student: $studentName"
        studentNameText.text = studentAutofill

        // Check if there's existing exam info
        if (dbHelper.isExamEntryExists(studentName!!)) {
            val cursor = dbHelper.getExamInfoByStudentName(studentName!!)
            if (cursor.moveToFirst()) {
                val savedExamDate = cursor.getString(cursor.getColumnIndexOrThrow("exam_date"))
                val savedExamName = cursor.getString(cursor.getColumnIndexOrThrow("exam_name"))
                val savedCatTwo = cursor.getInt(cursor.getColumnIndexOrThrow("cat_two")).toString()
                val savedCatOne = cursor.getInt(cursor.getColumnIndexOrThrow("cat_one")).toString()
                val savedFinalExam = cursor.getInt(cursor.getColumnIndexOrThrow("final_exam")).toString()
                val savedComments = cursor.getString(cursor.getColumnIndexOrThrow("comments"))
                examDateInput.setText(savedExamDate)
                examNameInput.setText(savedExamName)
                catOneInput.setText(savedCatOne)
                catTwoInput.setText(savedCatTwo)
                finalExamInput.setText(savedFinalExam)
                commentsInput.setText(savedComments)
            }
            cursor.close()
        }

        // Save button logic
        saveButton.setOnClickListener {
            val examDate = examDateInput.text.toString()
            val examName = examNameInput.text.toString()
            val catOne = catOneInput.text.toString().toIntOrNull()
            val catTwo = catTwoInput.text.toString().toIntOrNull()
            val finalExam = finalExamInput.text.toString().toIntOrNull()
            val comments = commentsInput.text.toString().ifEmpty { null }

            if (examDate.isEmpty() || examName.isEmpty()) {
                Toast.makeText(requireContext(), "Date and exam name are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = if (dbHelper.isExamEntryExists(studentName!!)) {
                dbHelper.updateExamEntry(
                    examDate,
                    examName,
                    studentName!!,
                    catOne,
                    catTwo,
                    finalExam,
                    comments
                )
            } else {
                dbHelper.createExamEntry(
                    examDate,
                    examName,
                    studentName!!,
                    catOne,
                    catTwo,
                    finalExam,
                    comments
                )
            }

            if (success) {
                Toast.makeText(requireContext(), "Exam details saved successfully.", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to save exam details.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
