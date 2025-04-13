package com.imnotndesh.schoolsync.adminPages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.MainActivity
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class EditAdminFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_admin, container, false)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        val currentUsername = sharedPreferences.getString("username", null)

        usernameEditText = view.findViewById(R.id.editUsername)
        passwordEditText = view.findViewById(R.id.editPassword)
        updateButton = view.findViewById(R.id.btnUpdate)
        deleteButton = view.findViewById(R.id.btnDelete)

        usernameEditText.setText(currentUsername)

        updateButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newPassword = passwordEditText.text.toString().trim()

            if (currentUsername != null && newUsername.isNotEmpty() && newPassword.isNotEmpty()) {
                val success = dbHelper.editAdminByUsername(currentUsername, newUsername, newPassword)
                if (success) {
                    sharedPreferences.edit().putString("username", newUsername).apply()
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            if (currentUsername != null) {
                val success = dbHelper.deleteAdminByUsername(currentUsername)
                if (success) {
                    sharedPreferences.edit().clear().apply()
                    Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
