all:
	javac -cp duckdb_jdbc-1.1.3.jar EnrollmentApp.java
	java -cp .:duckdb_jdbc-1.1.3.jar EnrollmentApp

database:
	duckdb cs348 < makeTables.sql; duckdb cs348 < populateTables.sql

submit:
	marmoset_submit cs348 A2\ -\ Part\ 2 EnrollmentApp.java
