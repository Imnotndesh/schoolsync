package com.imnotndesh.schoolsync.database

const val StudentTable = """
CREATE TABLE IF NOT EXISTS students (
    students_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    student_name TEXT NOT NULL UNIQUE,
    date_of_birth TEXT NOT NULL,
    parent_name TEXT default 'unassigned',
    gender TEXT NOT NULL,
    phone TEXT,
    class_name TEXT NOT NULL DEFAULT 'Unassigned'
);
"""

const val TeacherTable = """
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    teacher_name TEXT NOT NULL UNIQUE,
    email TEXT,
    subject TEXT NOT NULL,
    phone TEXT NOT NULL,
    class_name TEXT NOT NULL UNIQUE DEFAULT 'Unassigned',
    username TEXT NOT NULL,
    password TEXT NOT NULL
);
"""

const val AdminTable = """
CREATE TABLE IF NOT EXISTS admins (
    admin_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL
);
"""

const val ClassesTable = """
CREATE TABLE IF NOT EXISTS classes (
    class_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    class_name TEXT NOT NULL UNIQUE ,
    class_stream TEXT NOT NULL ,
    grade INTEGER NOT NULL ,
    teacher_name TEXT NOT NULL UNIQUE,
    capacity INTEGER NOT NULL
);
"""

const val ExamTable = """
CREATE TABLE IF NOT EXISTS exams (
    exam_id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
    exam_date TEXT NOT NULL,
    student_name INTEGER NOT NULL UNIQUE,
    cat_one INTEGER NOT NULL DEFAULT 0,
    cat_two INTEGER NOT NULL DEFAULT 0,
    final_exam INTEGER NOT NULL DEFAULT 0,
    comments TEXT DEFAULT 'No comments'
);
"""

const val ParentsTable = """
CREATE TABLE IF NOT EXISTS parents (
    parent_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    parent_name TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    phone TEXT NOT NULL,
    student_name TEXT NOT NULL
);
"""

const val AttendanceTable = """
CREATE TABLE IF NOT EXISTS attendance (
    date_taken TEXT NOT NULL PRIMARY KEY,
    present INTEGER NOT NULL,
    class_name TEXT NOT NULL,
    absent INTEGER DEFAULT 0,
    students_missing TEXT DEFAULT 'none'
);
"""

const val RemindersTable = """
CREATE TABLE IF NOT EXISTS reminders (
    reminder_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    reminder_text TEXT NOT NULL,
    creation_date TEXT NOT NULL,
    reminder_date TEXT NOT NULL,
    reminder_owner TEXT NOT NULL,
    owner_type TEXT NOT NULL
);
"""