package com.imnotndesh.schoolsync.teacherFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.MainActivity
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import com.imnotndesh.schoolsync.teacherFragments.teacherPages.EditTeacherFragment

class TeacherProfileFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var teacherNameText: TextView
    private lateinit var subjectText: TextView
    private lateinit var phoneText: TextView
    private lateinit var emailText: TextView
    private lateinit var classText: TextView
    private lateinit var logoutBtn: Button
    private lateinit var editTeacherBtn: Button
    private lateinit var profileImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_profile, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        teacherNameText = view.findViewById(R.id.teacherNameText)
        subjectText = view.findViewById(R.id.subjectText)
        phoneText = view.findViewById(R.id.phoneText)
        emailText = view.findViewById(R.id.emailText)
        classText = view.findViewById(R.id.classText)
        logoutBtn = view.findViewById(R.id.logoutButtonTeacher)
        profileImage = view.findViewById(R.id.profileImage)
        editTeacherBtn = view.findViewById(R.id.editTeacherButton)

        if (username != null) {
            val cursor: Cursor = dbHelper.getTeachersByUsername(username)
            if (cursor.moveToFirst()) {
                val teacherName = "Name: ${cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))}"
                val subjectName=  "Subject: ${cursor.getString(cursor.getColumnIndexOrThrow("subject"))}"
                val storedPhoneText = "Phone: ${cursor.getString(cursor.getColumnIndexOrThrow("phone"))}"
                val storedEmailText = "Email: ${cursor.getString(cursor.getColumnIndexOrThrow("email"))}"
                val className =  "Class: ${cursor.getString(cursor.getColumnIndexOrThrow("class_name"))}"
                teacherNameText.text = teacherName
                subjectText.text = subjectName
                phoneText.text = storedPhoneText
                emailText.text = storedEmailText
                classText.text =className
            }
            cursor.close()
        }
        editTeacherBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.teacher_fragment_container, EditTeacherFragment())
                .addToBackStack(null)
                .commit()
        }
        logoutBtn.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.let {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
        return view
    }
}
