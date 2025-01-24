--question5.sql: change the statement below.
SELECT
    COUNT(*) AS student_count
FROM
    student
WHERE
    snum IN (
        SELECT
            snum
        FROM
            enrolled
        WHERE
            cname IN (
                SELECT
                    cname
                FROM
                    enrolled
                GROUP BY
                    cname
                HAVING
                    COUNT(*) < 4
            )
        GROUP BY
            snum
        HAVING
            COUNT(*) = 1
    );