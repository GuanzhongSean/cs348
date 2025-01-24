--question4.sql: change the statement below.
SELECT
    c.name AS class_name,
    f.fname AS lecturer_name,
    COUNT(e.snum) AS class_size
FROM
    class c
    NATURAL JOIN faculty f
    LEFT OUTER JOIN enrolled e ON c.name = e.cname
GROUP BY
    c.name,
    f.fname
ORDER BY
    class_size DESC;