--question6.sql: change the statement below.
DELETE FROM
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
            COUNT(*) < 3
    );

DELETE FROM
    class
WHERE
    name NOT IN (
        SELECT
            cname
        FROM
            enrolled
    );