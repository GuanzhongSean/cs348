INSERT INTO student SELECT * FROM read_csv('student.csv');
SELECT * FROM student;

INSERT INTO faculty SELECT * FROM read_csv('faculty.csv');
SELECT * FROM faculty;

INSERT INTO class SELECT * FROM read_csv('class.csv');
SELECT * FROM class;

INSERT INTO enrolled SELECT * FROM read_csv('enrolled.csv');
SELECT * FROM enrolled;
