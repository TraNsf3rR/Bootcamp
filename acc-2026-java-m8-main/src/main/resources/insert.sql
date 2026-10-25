PRAGMA foreign_keys = ON;

-- Delete students first because students depends on courses.
DELETE FROM students;
DELETE FROM courses;

INSERT INTO courses (course_id, course_name, credits) VALUES
(1, 'Physics', 6),
(2, 'Biology', 4),
(3, 'Doing fun stuff', 22);

INSERT INTO students (name, email, age, course_id) VALUES
('Jānis', 'janis.berzins@inbox.lv', 19, 3),
('Baiba', 'baiba.klavina@inbox.lv', 43, 2),
('Ilzīte', 'ilze.joka@inbox.lv', 32, 3),
('Inguss', 'pienins83@inbox.lv', 18, 3),
('Mihails', 'biezpiens11@inbox.lv', 63, 2);

-- INSERT INTO students (id, name, email, age, course_id)
-- VALUES (6, 'Invalid Course Student', 'invalid@example.com', 20, 999);
