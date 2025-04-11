package com.imnotndesh.schoolsync.database

data class Student(
    val studentId: Int = 0,
    val studentName: String,
    val dateOfBirth: String,
    val parentName: String,
    val gender: String,
    val phone: String?,
    val className: String
)

data class Teacher(
    val teacherId: Int = 0,
    val teacherName: String,
    val email: String?,
    val subject: String,
    val phone: String,
    val className: String,
    val username: String,
    val password: String
)

data class AdminData(
    val adminId: Int = 0,
    var username: String,
    var password: String
)

data class SchoolClass(
    val classId: Int = 0,
    val className: String,
    val classStream: String,
    val grade: Int,
    val teacherName: String,
    val capacity: Int
)

data class Exam(
    val examId: Int = 0,
    val examDate: String,
    val studentName: Int,
    val catOne: Int,
    val catTwo: Int,
    val finalExam: Int,
    val comments: String?
)

data class Parent(
    val parentId: Int = 0,
    val parentName: String,
    val email: String,
    val phone: String,
    val studentName: String
)

data class Attendance(
    val dateTaken: String,
    val present: Int,
    val absent: Int,
    val studentsMissing: String,
    val className : String
)

data class Reminder(
    val reminderId: Int = 0,
    val reminderText: String,
    val creationDate: String,
    val reminderDate: String,
    val reminderOwner: String,
    val ownerType: String
)
