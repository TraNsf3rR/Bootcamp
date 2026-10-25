PRAGMA foreign_keys = ON;

SELECT *
FROM courses;

SELECT *
FROM students;

SELECT *
FROM students
WHERE age > 20;

SELECT students.name, students.email, students.age, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id;

SELECT courses.course_id, courses.course_name, COUNT(students.id) AS student_count
FROM courses
LEFT JOIN students ON courses.course_id = students.course_id
GROUP BY courses.course_id, courses.course_name;

UPDATE students
SET age = 21
WHERE id = 1;

SELECT *
FROM students
WHERE id = 1;

UPDATE students
SET course_id = 1
WHERE id = 2;

SELECT students.id, students.name, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id
WHERE students.id = 2;

DELETE FROM students
WHERE id = 5;

SELECT students.id, students.name, students.email, students.age, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id
ORDER BY students.id;

