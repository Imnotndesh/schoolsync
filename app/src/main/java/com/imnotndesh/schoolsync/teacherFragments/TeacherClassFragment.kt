package com.imnotndesh.schoolsync.teacherFragments

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.teacherFragments.teacherPages.*


class TeacherClassFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_class, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)

        val classNameText: TextView = view.findViewById(R.id.classNameText)
        val classStreamText: TextView = view.findViewById(R.id.classStreamText)
        val gradeText: TextView = view.findViewById(R.id.gradeText)
        val teacherNameText: TextView = view.findViewById(R.id.teacherNameText)
        val capacityText: TextView = view.findViewById(R.id.capacityText)

        val cursor: Cursor = dbHelper.getClassByTeacherUsername(username!!)
        if (cursor.moveToFirst()) {
            val newClassText : String = "Class Name: ${cursor.getString(cursor.getColumnIndexOrThrow("class_name"))}"
            val newClassStreamText : String = "Class Stream: ${cursor.getString(cursor.getColumnIndexOrThrow("class_stream"))}"
            val newGradeText : String = "Grade: ${cursor.getInt(cursor.getColumnIndexOrThrow("grade"))}"
            val newTeacherNameText : String = "Teacher: ${cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))}"
            val newCapacityText : String = "Capacity: ${cursor.getInt(cursor.getColumnIndexOrThrow("capacity"))}"

            classNameText.text = newClassText
            classStreamText.text = newClassStreamText
            gradeText.text = newGradeText
            teacherNameText.text = newTeacherNameText
            capacityText.text = newCapacityText
        } else {
            Toast.makeText(requireContext(), "No class found for this teacher.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        view.findViewById<Button>(R.id.parentInformationBttn).setOnClickListener {
            moveToFragment(ParentInformationFragment())
        }
        view.findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
           moveToFragment(StudentEnrollmentFragment())
        }
        view.findViewById<Button>(R.id.btnUpdateExam).setOnClickListener {
            moveToFragment(EditClassStudentMarksFragment())
        }
        view.findViewById<Button>(R.id.btnModifyStudent).setOnClickListener {
            moveToFragment(EditClassStudentFragment())
        }

        return view
    }
    private fun moveToFragment(fragment: Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.teacher_fragment_container,fragment)
            .addToBackStack(null)
            .commit()
    }
}
