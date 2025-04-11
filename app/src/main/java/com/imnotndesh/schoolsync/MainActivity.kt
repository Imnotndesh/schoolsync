package com.imnotndesh.schoolsync

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class MainActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var loginUsernameText: EditText
    private lateinit var loginPasswordText: EditText
    private lateinit var userTypeSpinner: Spinner
    private lateinit var signupTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var dbHelper: SchoolDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginButton = findViewById(R.id.loginButton)
        loginUsernameText = findViewById(R.id.loginUsernameText)
        loginPasswordText = findViewById(R.id.loginPasswordText)
        userTypeSpinner = findViewById(R.id.userTypeSpinner)
        signupTextView = findViewById(R.id.textView)

        // Adapter for user type spinner
        val userTypes = listOf("Admin", "Teacher")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, userTypes)
        userTypeSpinner.adapter = adapter

        // Logged in user checking
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("username", null)
        val loggedInUserType = sharedPreferences.getString("userType", null)
        if (loggedInUsername != null && loggedInUserType != null){
            moveToHomepage(loggedInUserType)
        }


        // If nobody has logged in
        dbHelper = SchoolDbHelper(this)
        signupTextView.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val username = loginUsernameText.text.toString()
            val password = loginPasswordText.text.toString()
            when (val userType = userTypeSpinner.selectedItem.toString()) {
                "Admin" -> {
                    val success = dbHelper.checkAdminPassword(username, password)
                    if (success){
                        moveToHomepage(userType)
                        saveToSession(username, userType)
                    }else{
                        Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                    }
                }
                "Teacher" -> {
                    val success = dbHelper.checkTeacherPassword(username,password)
                    if (success){
                        moveToHomepage(userType)
                        saveToSession(username, userType)
                    }else{
                        Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun saveToSession(username: String, userType: String){
        val editor = sharedPreferences.edit()
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