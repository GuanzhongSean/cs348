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
            enrolled
            JOIN class ON cname = name
        WHERE
            fid IN (
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