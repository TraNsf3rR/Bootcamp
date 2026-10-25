PRAGMA foreign_keys = ON;

-- Drop students first because students depends on courses.
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;

CREATE TABLE courses
(
    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_name TEXT NOT NULL,
    credits INTEGER CHECK ( credits > 0 )
);

CREATE TABLE students
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    age INTEGER CHECK ( age >= 18 ),
    course_id INTEGER NOT NULL,
    CONSTRAINT fk_course
    FOREIGN KEY (course_id)
    REFERENCES courses(course_id)
);

