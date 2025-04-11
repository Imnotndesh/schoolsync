package com.imnotndesh.schoolsync.adminFragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.imnotndesh.schoolsync.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imnotndesh.schoolsync.database.SchoolDbHelper

class AdminHomeFragment : Fragment() {

    private lateinit var dbHelper: SchoolDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvWelcome: TextView

    data class TableStat(
        val tableName: String,
        val count: Int
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = SchoolDbHelper(requireContext())
        recyclerView = view.findViewById(R.id.rvTableStats)
        tvWelcome = view.findViewById(R.id.tvWelcomeAdmin)

        val prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Admin") ?: "Admin"
        val headerText = "Welcome $username 🎉"
        tvWelcome.text = headerText

        val tableNames = listOf("students", "teachers", "reminders", "parents", "exams")
        val stats = tableNames.map { table ->
            TableStat(table.capitalize(), dbHelper.getTableCount(table))
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TableStatsAdapter(stats)
    }
    class TableStatsAdapter(private val tableStats: List<TableStat>) :
        RecyclerView.Adapter<TableStatsAdapter.TableStatViewHolder>() {

        inner class TableStatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tableName: TextView = view.findViewById(R.id.tvTableName)
            val tableCount: TextView = view.findViewById(R.id.tvTableCount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableStatViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_table_stat, parent, false)
            return TableStatViewHolder(view)
        }

        override fun onBindViewHolder(holder: TableStatViewHolder, position: Int) {
            val item = tableStats[position]
            val itemCount = item.count.toString()
            val capitalizedTable = item.tableName.capitalize()
            holder.tableName.text = capitalizedTable
            holder.tableCount.text = itemCount
        }

        override fun getItemCount() = tableStats.size
    }

}
