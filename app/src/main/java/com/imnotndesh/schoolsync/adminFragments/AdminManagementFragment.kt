package com.imnotndesh.schoolsync.adminFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.adminFragments.management_fragments.*

class AdminManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_management, container, false)

        // Teacher buttons
        val btnAddTeacher = view.findViewById<Button>(R.id.btnAddTeacher)
        val btnEditTeacher = view.findViewById<Button>(R.id.btnEditTeacher)
        val btnAssignClassTeacher = view.findViewById<Button>(R.id.btnAssignClassTeacher)
        val btnDeleteTeacher = view.findViewById<Button>(R.id.btnDeleteTeacher)

        // Student buttons
        val btnAddStudent = view.findViewById<Button>(R.id.btnAddStudent)
        val btnEditStudent = view.findViewById<Button>(R.id.btnEditStudent)
        val btnChangeClass = view.findViewById<Button>(R.id.btnChangeClass)
        val btnRemoveStudent = view.findViewById<Button>(R.id.btnRemoveStudent)

        // Class buttons
        val btnAddClass = view.findViewById<Button>(R.id.btnAddClass)
        val btnEditClass = view.findViewById<Button>(R.id.btnEditClass)
        val btnChangeTeacher = view.findViewById<Button>(R.id.btnChangeTeacher)
        val btnRemoveClass = view.findViewById<Button>(R.id.btnSearchClass)

        // Teacher button listeners
        btnAddTeacher.setOnClickListener { fragment_switcher_helper(AddTeacherFragment())}
        btnEditTeacher.setOnClickListener { fragment_switcher_helper(EditTeacherFragment())}
        btnAssignClassTeacher.setOnClickListener { fragment_switcher_helper(AssignTeacherNewClassFragment())}
        btnDeleteTeacher.setOnClickListener { DeleteTeacherFragment()}

        // Student button listeners
        btnAddStudent.setOnClickListener { AddStudentFragment()}
        btnEditStudent.setOnClickListener { EditStudentFragment()}
        btnChangeClass.setOnClickListener { ChangeStudentClassFragment()}
        btnRemoveStudent.setOnClickListener { RemoveStudentFragment()}

        // Class button listeners
        btnAddClass.setOnClickListener { AddClassFragment()}
        btnEditClass.setOnClickListener { EditClassFragment()}
        btnChangeTeacher.setOnClickListener { ChangeClassTeacherFragment()}
        btnRemoveClass.setOnClickListener { RemoveClassFragment()}

        return view
    }
    private fun fragment_switcher_helper(fragment: Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container,fragment)
            .addToBackStack(null)
            .commit()
    }
}
