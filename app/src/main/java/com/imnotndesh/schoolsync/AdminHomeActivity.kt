package com.imnotndesh.schoolsync
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imnotndesh.schoolsync.adminFragments.*

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container,AdminHomeFragment())
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.admin_bottom_nav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.admin_nav_home -> supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container, AdminHomeFragment()).commit()
                R.id.admin_nav_manage -> supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container,AdminManagementFragment()).commit()
                R.id.admin_nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container,AdminProfileFragment()).commit()
            }
            true
        }
    }
}
