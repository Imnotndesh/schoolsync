package com.imnotndesh.schoolsync.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SchoolDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "school_db"
        const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_STUDENTS = "students"
        const val TABLE_TEACHERS = "teachers"
        const val TABLE_ADMINS = "admins"
        const val TABLE_CLASSES = "classes"
        const val TABLE_EXAMS = "exams"
        const val TABLE_PARENTS = "parents"
        const val TABLE_ATTENDANCE = "attendance"
        const val TABLE_REMINDERS = "reminders"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create all tables
        db?.execSQL(StudentTable)
        db?.execSQL(TeacherTable)
        db?.execSQL(AdminTable)
        db?.execSQL(ClassesTable)
        db?.execSQL(ExamTable)
        db?.execSQL(ParentsTable)
        db?.execSQL(AttendanceTable)
        db?.execSQL(RemindersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMINS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXAMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PARENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REMINDERS")
        onCreate(db)
    }
    fun getAdminByUsername(username: String) : Cursor{
        val db = this.readableDatabase
        val selection = "username = ?"
        val selectionArgs = arrayOf(username)
        return db.query(TABLE_ADMINS, null, selection, selectionArgs, null, null, null)
    }
    fun getTeacherByUsername(username: String) : Cursor {
        val db = this.readableDatabase
        val selection = "username = ?"
        val selectionArgs = arrayOf(username)
        return db.query(TABLE_TEACHERS, null, selection, selectionArgs, null, null,null)
    }
    fun checkAdminPassword(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMINS,
            arrayOf("username","password"),
            "username = ? and password = ?",
            arrayOf(username,password),
            null,null,null
        )
        val isPasswordCorrect = cursor.count>0
        cursor.close()
        return isPasswordCorrect
    }
    fun checkTeacherPassword(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TEACHERS,
            arrayOf("username", "password"),
            "username = ? and password = ?",
            arrayOf(username, password),
            null, null, null
        )
        val isPasswordCorrect = cursor.count > 0
        cursor.close()
        return isPasswordCorrect
    }
    fun getTeachersByUsername(username: String): Cursor {
        val db = this.readableDatabase
        val selection = "username = ?"
        val selectionArgs = arrayOf(username)
        return db.query(TABLE_TEACHERS, null, selection, selectionArgs, null, null, null)
    }
    fun editTeacherByUsername(
        username: String,
        newTeacherName: String,
        newSubject: String,
        newEmail: String,
        newPhone: String,
        newClassName: String,
        newPassword: String
        ): Boolean {
        val exists = getTeacherByUsername(username)
        if (!exists.moveToFirst()){
            return false
        }
        exists.close()
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("teacher_name", newTeacherName)
            put("subject", newSubject)
            put("email", newEmail)
            put("phone", newPhone)
            put("class_name", newClassName)
            put("password", newPassword)
            put("username", username)
        }
        return db.update(TABLE_TEACHERS, values, "username = ?", arrayOf(username)) > 0
    }
    fun createTeacher(teacherName : String, username: String, password: String, className: String, subject: String, email: String, phone: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("teacher_name", teacherName)
            put("email", email)
            put("subject",subject)
            put("phone", phone)
            put("class_name",className)
            put("username", username)
            put("password", password)
        }
        return db.insert(TABLE_TEACHERS, null, values) != -1L
    }
    fun createAdmin(username: String,password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        return db.insert(TABLE_ADMINS, null, values) != -1L
    }
    fun editAdminByUsername(username: String, newUsername: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", newUsername)
            put("password", newPassword)
        }
        return db.update(TABLE_ADMINS, values, "username = ?", arrayOf(username)) > 0
    }
    fun deleteTeacherByUsername(username: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "username = ?"
        val whereArgs = arrayOf(username)
        return db.delete(TABLE_TEACHERS, whereClause, whereArgs) > 0
    }
    fun deleteAdminByUsername(username: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "username = ?"
        val whereArgs = arrayOf(username)
        return db.delete(TABLE_ADMINS, whereClause, whereArgs) > 0
    }
    fun createStudent(studentName: String,dateOfBirth: String,parentName: String,gender: String,phone: String?,className: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("student_name", studentName)
            put("date_of_birth", dateOfBirth)
            put("parent_name", parentName)
            put("gender", gender)
            put("phone", phone)
            put("class_name",className)
        }
        return db.insert(TABLE_STUDENTS, null, values) != -1L
    }
    fun getStudentByName(studentName: String): Cursor {
        val db = this.readableDatabase
        val selection = "student_name = ?"
        val selectionArgs = arrayOf(studentName)
        return db.query(TABLE_STUDENTS, null, selection, selectionArgs, null, null, null)
    }
    fun createClasses(schoolClass: SchoolClass): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("class_name", schoolClass.className)
            put("class_stream", schoolClass.classStream)
            put("grade", schoolClass.grade)
            put("teacher_name", schoolClass.teacherName)
            put("capacity", schoolClass.capacity)
        }
        return db.insert(TABLE_CLASSES, null, values) != -1L
    }
    fun createExam(exam: Exam): Boolean {
        val studentCursor = getStudentByName(exam.studentName.toString())
        studentCursor.use {
            if (it.moveToFirst()) return false
        }
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("exam_date", exam.examDate)
            put("student_name", exam.studentName)
        }
        return db.insert(TABLE_EXAMS, null, values) != -1L
    }
    fun editExamByStudentName(
        studentName: String,
        catOne: Int? = null,
        catTwo: Int? = null,
        finalExam: Int? = null
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        catOne?.let { values.put("cat_one", it) }
        catTwo?.let { values.put("cat_two", it) }
        finalExam?.let { values.put("final_exam", it) }
        if (values.size() == 0) {
            return false
        }
        val result = db.update(
            "exams",
            values,
            "student_name = ?",
            arrayOf(studentName)
        )
        db.close()
        return result > 0
    }
    fun deleteExamByStudentName(studentName: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "student_name = ?"
        val whereArgs = arrayOf(studentName)
        return db.delete(TABLE_EXAMS, whereClause, whereArgs) > 0
    }
    fun editClassByTeacherUserName(username: String, teacherClass:SchoolClass) :Boolean{
        val teacherCursor = getTeacherByUsername(username)
        val storedTeacherName: String
        var storedClassName: String
        teacherCursor.use {
            if (it.moveToFirst()) {
                storedTeacherName = it.getString(it.getColumnIndexOrThrow("teacher_name"))
                storedClassName = it.getString(it.getColumnIndexOrThrow("class_name"))
            }else{
                return false
            }
            teacherCursor.close()
        }
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("class_name", teacherClass.className)
            put("class_stream", teacherClass.classStream)
            put("grade", teacherClass.grade)
            put("teacher_name", teacherClass.teacherName)
            put("capacity", teacherClass.capacity)
        }
        return db.update(TABLE_CLASSES, values, "teacher_name = ? AND class_name = ?", arrayOf(storedTeacherName, storedClassName)) > 0
    }
    fun setAttendanceForTeacherClassByUsername(username:String,attendance: Attendance): Boolean {
        val db = writableDatabase
        val teacherCursor = getTeacherByUsername(username)
        val storedClassName: String
        teacherCursor.use {
            if (it.moveToFirst()) {
                storedClassName = it.getString(it.getColumnIndexOrThrow("class_name"))
            } else {
                return false
            }
            teacherCursor.close()
        }
        val values = ContentValues().apply {
            put("date_taken", attendance.dateTaken)
            put("absent", attendance.absent)
            put("present", attendance.present)
            put("students_missing", attendance.studentsMissing)
            put("class_name", storedClassName)
        }
        return db.insert(TABLE_ATTENDANCE, null, values) != -1L
    }
    fun editAttendanceForTeacherClassByUsername(username:String,attendance: Attendance): Boolean {
        val db = writableDatabase
        val teacherCursor = getTeacherByUsername(username)
        val storedClassName: String
        teacherCursor.use {
            if (it.moveToFirst()) {
                storedClassName = it.getString(it.getColumnIndexOrThrow("class_name"))
            } else {
                return false
            }
        }
        val values = ContentValues().apply {
            put("absent", attendance.absent)
            put("present", attendance.present)
            put("students_missing", attendance.studentsMissing)
        }
        return db.update(TABLE_ATTENDANCE, values, "class_name = ? and date_taken = ?", arrayOf(storedClassName, attendance.dateTaken)) > 0
    }
    fun createReminderByTeacherUsername(username:String,reminder: Reminder): Boolean {
        val db = writableDatabase
        val teacherCursor = getTeacherByUsername(username)
        val storedTeacherName: String
        teacherCursor.use {
            if (it.moveToFirst()) {
                storedTeacherName = it.getString(it.getColumnIndexOrThrow("teacher_name"))
            } else {
                return false
            }
        }
        val values = ContentValues().apply {
            put("reminder_text", reminder.reminderText)
            put("creation_date", reminder.creationDate)
            put("reminder_date", reminder.reminderDate)
            put("reminder_owner", storedTeacherName)
            put("owner_type", "Teacher")
        }
        return db.insert(TABLE_REMINDERS, null, values) != -1L
    }
    fun createReminderByAdminUsername(username:String,reminder: Reminder): Boolean {
        val db = writableDatabase
        val adminCursor = getAdminByUsername(username)
        val storedAdminName: String
        adminCursor.use {
            if (it.moveToFirst()) {
                storedAdminName = it.getString(it.getColumnIndexOrThrow("username"))
            } else {
                return false
            }
            adminCursor.close()
        }
        val values = ContentValues().apply {
            put("reminder_text", reminder.reminderText)
            put("creation_date", reminder.creationDate)
            put("reminder_date", reminder.reminderDate)
            put("reminder_owner", storedAdminName)
            put("owner_type", "Admin")
        }
        return db.insert(TABLE_REMINDERS, null, values) != -1L
    }
    fun deleteReminderById(reminderId: Int): Boolean {
        val db = this.writableDatabase
        val whereClause = "reminder_id = ?"
        val whereArgs = arrayOf(reminderId.toString())
        return db.delete(TABLE_REMINDERS, whereClause, whereArgs) > 0
    }
    fun getTableCount(tableName: String): Int {
        val db = readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        return count
    }
}