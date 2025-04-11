package com.imnotndesh.schoolsync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imnotndesh.schoolsync.teacherFragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class TeacherHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_home)

        supportFragmentManager.beginTransaction()
            .replace(R.id.teacher_fragment_container,TeacherHomeFragment())
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.teacher_bottom_nav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.teacher_nav_home -> supportFragmentManager.beginTransaction().replace(R.id.teacher_fragment_container, TeacherHomeFragment()).commit()
                R.id.teacher_nav_class -> supportFragmentManager.beginTransaction().replace(R.id.teacher_fragment_container, TeacherClassFragment()).commit()
                R.id.teacher_nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.teacher_fragment_container, TeacherProfileFragment()).commit()
            }
            true
        }
    }
}
