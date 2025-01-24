--question3.sql: change the statement below.
SELECT
    sname
FROM
    student
WHERE
    snum IN (
        SELECT
            e1.snum
        FROM
            enrolled e1
            JOIN enrolled e2 ON e1.snum = e2.snum
        WHERE
            e1.cname = 'Computer Networks'
            AND e2.cname = 'Algorithms'
    )
ORDER BY
    sname ASC;