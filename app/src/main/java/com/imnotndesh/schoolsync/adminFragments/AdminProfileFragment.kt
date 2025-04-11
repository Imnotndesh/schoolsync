package com.imnotndesh.schoolsync.adminFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.imnotndesh.schoolsync.MainActivity
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class AdminProfileFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var tvUsername: TextView
    private lateinit var tvPassword: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername = view.findViewById(R.id.tvUsername)
        tvPassword = view.findViewById(R.id.tvPassword)
        logoutButton = view.findViewById(R.id.btnLogout)
        editProfileButton = view.findViewById(R.id.btnEditProfile)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPrefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", null)

        if (username != null) {
            val cursor = dbHelper.getAdminByUsername(username)
            cursor.use {
                if (it.moveToFirst()) {
                    val user = it.getString(it.getColumnIndexOrThrow("username"))
                    val pass = it.getString(it.getColumnIndexOrThrow("password"))

                    tvUsername.text = user
                    tvPassword.text = pass
                }
                cursor.close()
            }
            editProfileButton.setOnClickListener{
                parentFragmentManager.beginTransaction()
                    .replace(R.id.admin_fragment_container, EditAdminFragment()).commit()
            }
            logoutButton.setOnClickListener {
                sharedPrefs.edit().clear().apply()
                val intent = Intent(requireContext(),MainActivity::class.java)
                intent.let {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }
}
