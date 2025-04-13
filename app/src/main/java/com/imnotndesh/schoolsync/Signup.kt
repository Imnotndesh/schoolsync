package com.imnotndesh.schoolsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.imnotndesh.schoolsync.database.AdminData
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class Signup : AppCompatActivity() {

    private lateinit var roleSpinner: Spinner
    private lateinit var adminLayout: LinearLayout
    private lateinit var teacherLayout: LinearLayout

    private lateinit var adminUsername: EditText
    private lateinit var adminPassword: EditText

    private lateinit var teacherName: EditText
    private lateinit var teacherUsername: EditText
    private lateinit var teacherPassword: EditText
    private lateinit var subject: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var className: EditText

    private lateinit var registerBtn: Button
    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        sharedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE)
        val loggedInUsername = sharedPrefs.getString("username", null)
        val loggedInUserType = sharedPrefs.getString("userType", null)
        if (loggedInUsername != null && loggedInUserType != null){
            moveToHomepage(loggedInUserType)
        }
        dbHelper = SchoolDbHelper(this)

        roleSpinner = findViewById(R.id.role_spinner)
        adminLayout = findViewById(R.id.admin_fields)
        teacherLayout = findViewById(R.id.teacher_fields)

        adminUsername = findViewById(R.id.et_admin_username)
        adminPassword = findViewById(R.id.et_admin_password)

        teacherName = findViewById(R.id.et_teacher_name)
        teacherUsername = findViewById(R.id.et_teacher_username)
        teacherPassword = findViewById(R.id.et_teacher_password)
        subject = findViewById(R.id.et_subject)
        email = findViewById(R.id.et_email)
        phone = findViewById(R.id.et_phone)

        registerBtn = findViewById(R.id.btn_register)

        val roles = listOf("Admin", "Teacher")
        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (roles[position]) {
                    "Admin" -> {
                        adminLayout.visibility = View.VISIBLE
                        teacherLayout.visibility = View.GONE
                    }
                    "Teacher" -> {
                        adminLayout.visibility = View.GONE
                        teacherLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        registerBtn.setOnClickListener {
            when (roleSpinner.selectedItem.toString()) {
                "Admin" -> registerAdmin()
                "Teacher" -> registerTeacher()
            }
        }
    }

    private fun registerAdmin() {
        val userType = "Admin"
        val username = adminUsername.text.toString().trim()
        val password = adminPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all admin fields", Toast.LENGTH_SHORT).show()
            return
        }
        val result = dbHelper.createAdmin(username,password)
        if (result) {
            saveToSession(username,userType)
            Toast.makeText(this, "Admin registered successfully", Toast.LENGTH_SHORT).show()
            moveToHomepage(userType)
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerTeacher() {
        val userType ="Teacher"
        val name = teacherName.text.toString().trim()
        val username = teacherUsername.text.toString().trim()
        val password = teacherPassword.text.toString().trim()
        val subjectStr = subject.text.toString().trim()
        val emailStr = email.text.toString().trim()
        val phoneStr = phone.text.toString().trim()

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() ||
            subjectStr.isEmpty() || emailStr.isEmpty() || phoneStr.isEmpty()
        ) {
            Toast.makeText(this, "Fill all teacher fields", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.signupAsTeacher(name, username, password, subjectStr, emailStr, phoneStr)
        if (success) {
            saveToSession(username, userType)
            Toast.makeText(this, "Teacher registered successfully", Toast.LENGTH_SHORT).show()
            moveToHomepage(userType)
        } else {
            Toast.makeText(this, "Teacher registration failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveToSession(username: String, userType: String){
        val editor = sharedPrefs.edit()
        editor.putString("username", username)
        editor.putString("userType", userType)
        editor.apply()
    }
    private fun moveToHomepage(userType: String){
        val intent = when (userType) {
            "Admin" -> Intent(this, AdminHomeActivity::class.java)
            "Teacher" -> Intent(this, TeacherHomeActivity::class.java)
            else -> throw IllegalArgumentException("Unknown user type: $userType")
        }
        intent.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
        finish()
    }
}
