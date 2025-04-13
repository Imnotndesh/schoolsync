package com.imnotndesh.schoolsync.teacherFragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.imnotndesh.schoolsync.R
import com.imnotndesh.schoolsync.database.SchoolDbHelper
import android.content.SharedPreferences
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.teacherFragments.teacherPages.*

class TeacherHomeFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerTeacherStats: RecyclerView
    private lateinit var textWelcomeTeacher: TextView
    private lateinit var recyclerActionSuggestions: RecyclerView
    data class HomeCardItem(
        val title: String,
        val value: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = SchoolDbHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        textWelcomeTeacher = view.findViewById(R.id.textWelcomeTeacher)
        recyclerTeacherStats = view.findViewById(R.id.recyclerTeacherStats)
        recyclerActionSuggestions = view.findViewById(R.id.recyclerActionSuggestions)

        val username = sharedPreferences.getString("username", "Teacher") ?: "Teacher"
        val titlePref = "Welcome, $username"
        textWelcomeTeacher.text = titlePref

        loadTeacherStats(username)
        setupActionSuggestions()
    }

    private fun loadTeacherStats(username: String) {
        val studentsCount = dbHelper.countStudentsRegisteredInClassbyTeacherUsername(username)
        val pendingMarksCount = dbHelper.getPendingMarksCountByTeacherUsername(username)

        val statsList = listOf(
            HomeCardItem("Students in Class", studentsCount.toString()),
            HomeCardItem("Pending Exam Marks", pendingMarksCount.toString())
        )

        val adapter = HomeCardAdapter(statsList)
        recyclerTeacherStats.layoutManager = LinearLayoutManager(requireContext())
        recyclerTeacherStats.adapter = adapter
    }

    private fun setupActionSuggestions() {
        val actions = listOf(
            "Edit Students",
            "Manage Exams",
            "Take Attendance",
            "Edit Attendance",
            "Edit Profile",
            "Get Parent Information",
            "Enroll Student"
        )

        recyclerActionSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val actionAdapter = ActionSuggestionAdapter(actions)
        recyclerActionSuggestions.adapter = actionAdapter
    }

    // Inner Adapter: Action Suggestions
    inner class ActionSuggestionAdapter(private val actions: List<String>) :
        RecyclerView.Adapter<ActionSuggestionAdapter.ActionViewHolder>() {

        inner class ActionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button: Button = view.findViewById(R.id.buttonAction)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_action_suggestion, parent, false)
            return ActionViewHolder(view)
        }

        override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
            val action = actions[position]
            holder.button.text = action
            holder.button.setOnClickListener {
                onActionClick(action)
            }
        }

        override fun getItemCount() = actions.size
    }

    // Inner Adapter: Class Summary Stats
    inner class HomeCardAdapter(private val items: List<HomeCardItem>) :
        RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

        inner class HomeCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textTitle: TextView = view.findViewById(R.id.textCardTitle)
            val textValue: TextView = view.findViewById(R.id.textCardValue)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_card, parent, false)
            return HomeCardViewHolder(view)
        }

        override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
            val item = items[position]
            holder.textTitle.text = item.title
            holder.textValue.text = item.value
        }

        override fun getItemCount() = items.size
    }
    fun onActionClick(action: String){
        when (action) {
            "Edit Students" -> {
                moveToFragment(TeacherClassFragment())
            }
            "Manage Exams" -> {
                moveToFragment(TeacherClassFragment())
            }
            "Take Attendance" -> {
                moveToFragment(TakeAttendanceFragment())
            }
            "Edit Profile" -> {
                moveToFragment(TeacherProfileFragment())
            }
            "Get Parent Information" -> {
                moveToFragment(ParentInformationFragment())
            }
            "Enroll Student" -> {
                moveToFragment(StudentEnrollmentFragment())
            }
            "Edit Attendance" ->{
                moveToFragment(EditAttendanceFragment())
            }
        }
    }
    private fun moveToFragment(fragment: Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.teacher_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
