DROP TABLE IF EXISTS enrolled;
DROP TABLE IF EXISTS class;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS student;

CREATE TABLE student 
  ( 
     snum     DECIMAL(9, 0) NOT NULL PRIMARY KEY, 
     sname    VARCHAR(30), 
     major    VARCHAR(25), 
     standing VARCHAR(2), 
     age      DECIMAL(3, 0) 
  ); 

CREATE TABLE faculty 
  ( 
     fid    DECIMAL(9, 0) NOT NULL PRIMARY KEY, 
     fname  VARCHAR(30), 
     deptid DECIMAL(2, 0) 
  ); 

CREATE TABLE class 
  ( 
     name     VARCHAR(40) NOT NULL, 
     meets_at VARCHAR(20), 
     room     VARCHAR(10), 
     fid      DECIMAL(9, 0), 
     section  decimal(2, 0) DEFAULT 1 NOT NULL,
     PRIMARY KEY(name, section),
     FOREIGN KEY(fid) REFERENCES faculty 
  ); 

CREATE TABLE enrolled 
  ( 
     snum  DECIMAL(9, 0) NOT NULL, 
     cname VARCHAR(40) NOT NULL, 
     section  decimal(2, 0) DEFAULT 1 NOT NULL,
     PRIMARY KEY(snum, cname, section), 
     FOREIGN KEY(snum) REFERENCES student, 
     FOREIGN KEY(cname, section) REFERENCES class(name, section) 
  ); 

