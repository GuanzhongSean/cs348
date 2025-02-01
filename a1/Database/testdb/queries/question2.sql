--question2.sql: change the statement below.
SELECT
    fname
FROM
    faculty
WHERE
    fid NOT IN (
        SELECT
            DISTINCT fid
        FROM
            class
    )
ORDER BY
    fname ASC;