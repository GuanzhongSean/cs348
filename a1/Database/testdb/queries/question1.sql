--question1.sql:
SELECT
    AVG(age) AS average_age
FROM
    student
WHERE
    snum IN (
        SELECT
            DISTINCT snum
        FROM
            enrolled AS e
            JOIN class AS c ON e.cname = c.name
        WHERE
            c.fid IN (
                SELECT
                    fid
                FROM
                    class
                GROUP BY
                    fid
                HAVING
                    COUNT(*) >= 3
            )
    );