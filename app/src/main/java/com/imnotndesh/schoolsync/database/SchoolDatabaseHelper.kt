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
    fun getTeacherByTeacherName(teacherName: String): Cursor {
        val db = this.readableDatabase
        val selection = "teacher_name = ?"
        val selectionArgs = arrayOf(teacherName)
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
    fun editTeacherByTeacherName(
        teacherName: String,
        newSubject: String,
        newEmail: String,
        newPhone: String,
        newClassName: String,
        newPassword: String,
        newUsername: String
    ): Boolean {
        val exists = getTeacherByTeacherName(teacherName)
        if (!exists.moveToFirst()){
            return false
        }
        exists.close()
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", newUsername)
            put("subject", newSubject)
            put("email", newEmail)
            put("phone", newPhone)
            put("class_name", newClassName)
            put("password", newPassword)
            put("teacher_name", teacherName)
        }
        return db.update(TABLE_TEACHERS, values, "teacher_name = ?", arrayOf(teacherName)) > 0
    }
    fun signupAsTeacher(teacherName : String, username: String, password: String, subject: String, email: String, phone: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("teacher_name", teacherName)
            put("email", email)
            put("subject",subject)
            put("phone", phone)
            put("username", username)
            put("password", password)
        }
        return db.insert(TABLE_TEACHERS, null, values) != -1L
    }
    fun findParentInfoByStudentName(studentName: String ) :Cursor{
        val db = readableDatabase
        val selection = "student_name = ?"
        val selectionArgs = arrayOf(studentName)
        return db.query(TABLE_PARENTS, null, selection, selectionArgs, null, null, null)
    }
    fun isStudentInTeacherClass(username: String, studentName: String): Boolean {
        var teacherClassName: String? = ""
        val teacherCursor = getTeacherByUsername(username)
        teacherCursor.use {
            if (it.moveToFirst()) {
                teacherClassName = it.getString(it.getColumnIndexOrThrow("class_name"))
            }
            teacherCursor.close()
        }
        val studentCursor = getStudentByName(studentName)
        var studentClassName: String? = ""
        studentCursor.use{
            if (it.moveToFirst()) {
                studentClassName = it.getString(it.getColumnIndexOrThrow("class_name"))
            }
            studentCursor.close()
        }
        return teacherClassName == studentClassName
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
    fun createParent(parentName: String,email: String,phone: String,studentName: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("parent_name", parentName)
            put("email", email)
            put("phone", phone)
            put("student_name", studentName)
        }
        return db.insert(TABLE_PARENTS, null, values) != -1L
    }
    fun getClassNameByTeacherUsername(username: String): String {
        val teacherCursor = getTeacherByUsername(username)
        var className: String = ""
        teacherCursor.use {
            if (it.moveToFirst()) {
                className = it.getString(it.getColumnIndexOrThrow("class_name"))
            }
            teacherCursor.close()
        }
        return className
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
    fun editStudentByName(studentName: String, newDateOfBirth: String, newGender: String, newPhone: String?, newClassName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("date_of_birth", newDateOfBirth)
            put("gender", newGender)
            put("phone", newPhone)
            put("class_name", newClassName)
            put("student_name", studentName)
        }
        return db.update(TABLE_STUDENTS, contentValues, "student_name = ?", arrayOf(studentName)) > 0
    }
    fun changeStudentClassByStudentName(studentName: String, newClassName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("class_name", newClassName)
        }
        return db.update(TABLE_STUDENTS, contentValues, "student_name = ?", arrayOf(studentName)) > 0
    }
    fun changeTeacherClassByTeacherName(teacherName: String, newClassName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("class_name", newClassName)
        }
        return db.update(TABLE_TEACHERS, contentValues, "teacher_name = ?", arrayOf(teacherName)) > 0
    }
    fun changeClassTeacherByClassName(className: String, newTeacherName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("teacher_name", newTeacherName)
        }
        return db.update(TABLE_CLASSES, contentValues, "class_name = ?", arrayOf(className)) > 0
    }
    fun deleteStudentByName(studentName: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "student_name = ?"
        val whereArgs = arrayOf(studentName)
        return db.delete(TABLE_STUDENTS, whereClause, whereArgs) > 0
    }
    fun createClass(className: String,classStream: String,grade: String,teacherName: String,capacity: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("class_name", className)
            put("class_stream", classStream)
            put("grade", grade)
            put("teacher_name", teacherName)
            put("capacity", capacity)
        }
        return db.insert(TABLE_CLASSES, null, values) != -1L
    }
    fun getClassByTeacherUsername(username :String): Cursor{
        val teacherCursor = getTeacherByUsername(username)
        var storedTeacherName: String = ""
        teacherCursor.use{
            if (it.moveToFirst()) {
                storedTeacherName = it.getString(it.getColumnIndexOrThrow("teacher_name"))
            }
            teacherCursor.close()
        }
        val db = this.readableDatabase
        val selection = "teacher_name = ?"
        val selectionArgs = arrayOf(storedTeacherName)
        return db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, null)
    }
    fun getClassByClassName(className: String): Cursor {
        val db = this.readableDatabase
        val selection = "class_name = ?"
        val selectionArgs = arrayOf(className)
        return db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, null)
    }
    fun deleteClassByClassName(className: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "class_name = ?"
        val whereArgs = arrayOf(className)
        return db.delete(TABLE_CLASSES, whereClause, whereArgs) > 0
    }
    fun editClassByClassName(className: String,classStream: String,grade: String,teacherName: String,capacity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("class_stream", classStream)
            put("grade", grade)
            put("teacher_name", teacherName)
            put("capacity", capacity)
            put("class_name", className)
        }
        return db.update(TABLE_CLASSES, values, "class_name = ?", arrayOf(className)) > 0
    }
    fun createExamEntry(
        examDate: String,
        examName: String,
        studentName: String,
        catOne: Int? = null,
        catTwo: Int? = null,
        finalExam: Int? = null,
        comments: String? = null
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("exam_date", examDate)
            put("exam_name", examName)
            put("student_name", studentName)
            put("cat_one", catOne ?: 0)
            put("cat_two", catTwo ?: 0)
            put("final_exam", finalExam ?: 0)
            put("comments", comments ?: "No comments")
        }
        return db.insert(TABLE_EXAMS, null, values) != -1L
    }
    fun isExamEntryExists(studentName: String): Boolean {
        val db = this.readableDatabase
        val selection = "student_name = ?"
        val selectionArgs = arrayOf(studentName)
        val cursor = db.query(TABLE_EXAMS, null, selection, selectionArgs, null, null, null)
        val exists = cursor.count
        cursor.close()
        return exists > 0
    }
    fun getExamInfoByStudentName(studentName: String): Cursor {
        val db = this.readableDatabase
        val selection = "student_name = ?"
        val selectionArgs = arrayOf(studentName)
        return db.query(TABLE_EXAMS, null, selection, selectionArgs, null, null, null)
    }
    fun updateExamEntry(
        examDate: String,
        examName: String,
        studentName: String,
        catOne: Int? = null,
        catTwo: Int? = null,
        finalExam: Int? = null,
        comments: String? = null
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("exam_date", examDate)
            put("exam_name", examName)
            put("cat_one", catOne ?: 0)
            put("cat_two", catTwo ?: 0)
            put("final_exam", finalExam ?: 0)
            put("comments", comments ?: "No comments")
        }

        val whereClause = "student_name = ?"
        val whereArgs = arrayOf(studentName)

        val rowsAffected = db.update(TABLE_EXAMS, values, whereClause, whereArgs)
        return rowsAffected > 0
    }

    fun findStudentsByStudentNameLike(partialName: String): Cursor {
        val db = readableDatabase
        val selection = "student_name LIKE ?"
        val selectionArgs = arrayOf("%$partialName%")
        return db.query(TABLE_STUDENTS, null, selection, selectionArgs, null, null, null)
    }
    fun countStudentsRegisteredInClassbyTeacherUsername(username: String): Int {
        val className = getClassNameByTeacherUsername(username)
        val db = readableDatabase
        val selection = "class_name = ?"
        val selectionArgs = arrayOf(className)
        val cursor = db.query(TABLE_STUDENTS, null, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }
    fun getPendingMarksCountByTeacherUsername(username: String): Int {
        val db = readableDatabase
        val className = getClassNameByTeacherUsername(username)

        // Fetch all student names in that class
        val studentCursor = db.query(
            TABLE_STUDENTS,
            arrayOf("student_name"),
            "class_name = ?",
            arrayOf(className),
            null,
            null,
            null
        )

        val studentNames = mutableListOf<String>()
        studentCursor.use {
            while (it.moveToNext()) {
                studentNames.add(it.getString(it.getColumnIndexOrThrow("student_name")))
            }
        }

        if (studentNames.isEmpty()) return 0 // no students in class, no pending marks

        // Prepare placeholders for SQL IN clause
        val placeholders = studentNames.joinToString(",") { "?" }

        // Build selection query
        val selection = "student_name IN ($placeholders) AND (cat_one = 0 OR cat_two = 0 OR final_exam = 0)"
        val selectionArgs = studentNames.toTypedArray()

        // Query exams table for pending marks count
        val examCursor = db.query(
            TABLE_EXAMS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val count = examCursor.count
        examCursor.close()

        return count
    }

    fun takeAttendanceByTeacherUsername(username: String, dateTaken: String, absent: Int, present: Int, studentsMissing: String): Boolean {
        val className = getClassNameByTeacherUsername(username)
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date_taken", dateTaken)
            put("absent", absent)
            put("present", present)
            put("students_missing", studentsMissing)
            put("class_name", className)
        }
        return db.insert(TABLE_ATTENDANCE, null, values) != -1L
    }
    fun editAttendanceByTeacherUsername(username: String, dateTaken: String, absent: Int, present: Int, studentsMissing: String): Boolean {
        val className = getClassNameByTeacherUsername(username)
        val db = writableDatabase
        val values = ContentValues().apply {
            put("absent", absent)
            put("present", present)
            put("students_missing", studentsMissing)
        }
        return db.update(TABLE_ATTENDANCE, values, "class_name = ? and date_taken = ?", arrayOf(className, dateTaken)) > 0
    }
    fun isAttendanceEntryExistsForDate(username: String, dateTaken: String): Boolean {
        val className = getClassNameByTeacherUsername(username)
        val db = readableDatabase
        val selection = "class_name = ? AND date_taken = ?"
        val selectionArgs = arrayOf(className, dateTaken)
        val cursor = db.query(TABLE_ATTENDANCE, null, selection, selectionArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    fun getAttendanceByUsernameAndDate(username: String, dateTaken: String): Cursor {
        val className = getClassNameByTeacherUsername(username)
        val db = readableDatabase
        val selection = "class_name = ? AND date_taken = ?"
        val selectionArgs = arrayOf(className, dateTaken)
        return db.query(TABLE_ATTENDANCE, null, selection, selectionArgs, null, null, null)
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