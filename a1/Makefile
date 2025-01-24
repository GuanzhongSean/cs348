all:
	pdflatex a1.tex
	marmoset_submit cs348 A1\ -\ Part\ 2 Database/testdb/queries/*.sql

testDb:
	cd Database/testdb; duckdb testDb < createTables.sql; duckdb testDb < populateTables.sql

q%:
	cd Database/testdb; duckdb testDb < queries/question$*.sql
