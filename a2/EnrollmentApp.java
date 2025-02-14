import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

// sample input: localhost 5432 database_name username password
public class EnrollmentApp {
    public static final String CONFLICTING_COURSE = "[Warning] Conflicting classes";
    public static final String INVALID_ID = "[Error] Invalid ID";
    public static final String INVALID_CLASS = "[Error] Invalid class";
    public static final String INVALID_ENROLLMENT = "[Error] Invalid enrollment";
    public static final String NO_ENROLLMENT = "[Warning] No enrollment";

    private Scanner input = new Scanner(System.in);
    private Connection connection = null;

    public EnrollmentApp(String[] args) {

        // loading the DBMS driver
        try {
            Class.forName("org.duckdb.DuckDBDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Missing DBMS driver.");
            e.printStackTrace();
        }

        try {
            // connecting to the a database
            connection = DriverManager.getConnection("jdbc:duckdb:./cs348");
            System.out.println("Database connection open.\n");

            // setting auto commit to false
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("DBMS connection failed.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        EnrollmentApp menu = new EnrollmentApp(args);
        menu.mainMenu(args);
        menu.exit();
    }

    public void exit() {

        try {
            // close database connection
            connection.commit();
            connection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mainMenu(String[] args) throws SQLException {

        mainMenu: while (true) {

            System.out.println("\n-- Actions --");

            System.out.println("Select an option: \n" +
                    "  1) Get your classes\n" +
                    "  2) Search your classmates\n" +
                    "  3) Major statistics of your class\n" +
                    "  0) Exit\n ");
            int selection = input.nextInt();
            input.nextLine();

            switch (selection) {
                case 1:
                    System.out.print("Please provide the user ID: ");
                    int userID = input.nextInt();
                    input.nextLine();
                    this.getClassByStudentID(userID);
                    break;
                case 2:
                    System.out.print("Please provide the user ID: ");
                    int id = input.nextInt();
                    input.nextLine();
                    System.out.print("Please provide the list of class names: ");
                    String classes = input.nextLine();

                    this.searchCommonClassmate(id, classes);
                    break;
                case 3:
                    System.out.print("Please provide the class name: ");
                    String myclass = input.nextLine();

                    this.getClassStatis(myclass);
                    break;
                case 0:
                    System.out.println("Returning...\n");
                    break mainMenu;
                default:
                    System.out.println("Invalid action.");
                    break;
            }
        }
    }

    /**
     * (a)
     * <p>
     * Print to std a list of classes that a student has been enrolled in with an ID
     * number supplied as an integer argument to the command line. Print one class
     * per row and the section number the given student is enrolled in next to each
     * class (separated by tab). Sort the output on class name. Write your code
     * inside the getClassByStudentID function.
     *
     * @param userID id number of the student
     * @throws SQLException
     */
    private void getClassByStudentID(int userID) throws SQLException {
        // Check if the student ID is valid
        String checkStudentQuery = "SELECT * FROM student WHERE snum = ?";
        PreparedStatement checkStudentStmt = connection.prepareStatement(checkStudentQuery);
        checkStudentStmt.setInt(1, userID);
        ResultSet studentResult = checkStudentStmt.executeQuery();
        if (!studentResult.next()) {
            System.err.println(INVALID_ID);
            return;
        }

        // Get the classes the student is enrolled in
        String getClassesQuery = "SELECT\r\n" + //
                "    c.name,\r\n" + //
                "    c.section,\r\n" + //
                "    c.meets_at\r\n" + //
                "FROM\r\n" + //
                "    enrolled e\r\n" + //
                "    JOIN class c ON e.cname = c.name\r\n" + //
                "    AND e.section = c.section\r\n" + //
                "WHERE\r\n" + //
                "    e.snum = ?\r\n" + //
                "ORDER BY\r\n" + //
                "    c.name";
        PreparedStatement getClassesStmt = connection.prepareStatement(getClassesQuery);
        getClassesStmt.setInt(1, userID);
        ResultSet classesResult = getClassesStmt.executeQuery();
        if (!classesResult.next()) {
            System.err.println(NO_ENROLLMENT);
            return;
        } else {
            do {
                String className = classesResult.getString("name");
                int section = classesResult.getInt("section");
                System.out.println(className + "\t" + section);
            } while (classesResult.next());
        }

        // Check for time conflicts
        String conflictQuery = "SELECT\r\n" + //
                "    c1.name,\r\n" + //
                "    c1.meets_at\r\n" + //
                "FROM\r\n" + //
                "    enrolled e1\r\n" + //
                "    JOIN class c1 ON e1.cname = c1.name\r\n" + //
                "    AND e1.section = c1.section\r\n" + //
                "WHERE\r\n" + //
                "    e1.snum = ?\r\n" + //
                "    AND EXISTS (\r\n" + //
                "        SELECT\r\n" + //
                "            1\r\n" + //
                "        FROM\r\n" + //
                "            enrolled e2\r\n" + //
                "            JOIN class c2 ON e2.cname = c2.name\r\n" + //
                "            AND e2.section = c2.section\r\n" + //
                "        WHERE\r\n" + //
                "            e2.snum = e1.snum\r\n" + //
                "            AND c2.meets_at = c1.meets_at\r\n" + //
                "            AND c2.name <> c1.name\r\n" + //
                "    )\r\n" + //
                "ORDER BY\r\n" + //
                "    c1.meets_at,\r\n" + //
                "    c1.name";
        PreparedStatement conflictStmt = connection.prepareStatement(conflictQuery);
        conflictStmt.setInt(1, userID);
        ResultSet conflictResult = conflictStmt.executeQuery();
        if (conflictResult.next()) {
            System.err.println(CONFLICTING_COURSE);
            do {
                String className = conflictResult.getString("name");
                String meetsAt = conflictResult.getString("meets_at");
                System.out.println(className + "\t" + meetsAt);
            } while (conflictResult.next());
        }
    }

    /**
     * (b)
     * <p>
     * Given a student ID number and a list of class names separated with comma as
     * arguments on the command line, print to std in alphabet order the list of
     * student names who have been enrolled in all the given classes as the given
     * student has.
     *
     * @param userID  id number of the student
     * @param classes a list of class names separated with comma
     * @throws SQLException
     */
    private void searchCommonClassmate(int userID, String classes) throws SQLException {
        // Check if the student ID is valid
        String checkStudentQuery = "SELECT * FROM student WHERE snum = ?";
        PreparedStatement checkStudentStmt = connection.prepareStatement(checkStudentQuery);
        checkStudentStmt.setInt(1, userID);
        ResultSet studentResult = checkStudentStmt.executeQuery();
        if (!studentResult.next()) {
            System.err.println(INVALID_ID);
            return;
        }

        String[] classArray = classes.split(",");
        List<Integer> sectionArray = new ArrayList<>();
        // If no classes are provided, get all classes the student is enrolled in
        if (classes.isEmpty()) {
            String getClassesQuery = "SELECT cname, section FROM enrolled WHERE snum = ?";
            PreparedStatement getClassesStmt = connection.prepareStatement(getClassesQuery);
            getClassesStmt.setInt(1, userID);
            ResultSet classesResult = getClassesStmt.executeQuery();
            if (!classesResult.next()) {
                System.err.println(NO_ENROLLMENT);
                return;
            } else {
                do {
                    String className = classesResult.getString("cname");
                    classes += className + ",";
                    sectionArray.add(classesResult.getInt("section"));
                } while (classesResult.next());
            }
            classArray = classes.split(",");
        } else {
            // Check if all provided classes are valid
            for (String className : classArray) {
                String checkClassQuery = "SELECT * FROM class WHERE name = ?";
                PreparedStatement checkClassStmt = connection.prepareStatement(checkClassQuery);
                checkClassStmt.setString(1, className.trim());
                ResultSet classResult = checkClassStmt.executeQuery();
                if (!classResult.next()) {
                    System.err.println(INVALID_CLASS);
                    return;
                }
            }

            // Check if the student is enrolled in all provided classes
            for (String className : classArray) {
                String checkEnrollmentQuery = "SELECT * FROM enrolled WHERE snum = ? AND cname = ?";
                PreparedStatement checkEnrollmentStmt = connection.prepareStatement(checkEnrollmentQuery);
                checkEnrollmentStmt.setInt(1, userID);
                checkEnrollmentStmt.setString(2, className.trim());
                ResultSet enrollmentResult = checkEnrollmentStmt.executeQuery();
                if (!enrollmentResult.next()) {
                    System.err.println(INVALID_ENROLLMENT);
                    return;
                } else {
                    sectionArray.add(enrollmentResult.getInt("section"));
                }
            }
        }

        // Find classmates who are enrolled in all the same classes (and sections)
        String commonClassmateQuery = "SELECT * FROM (";
        for (int i = 0; i < classArray.length; i++) {
            if (i != 0) {
                commonClassmateQuery += " INTERSECT ";
            }
            commonClassmateQuery += "SELECT s.sname FROM enrolled e JOIN student s ON e.snum = s.snum WHERE e.cname = \'"
                    + classArray[i] + "\'";
            // commonClassmateQuery += " AND e.section = " + sectionArray.get(i);
        }
        commonClassmateQuery += ") ORDER BY sname";
        Statement commonClassmateStmt = connection.createStatement();
        ResultSet commonClassmateResult = commonClassmateStmt.executeQuery(commonClassmateQuery);
        while (commonClassmateResult.next()) {
            System.out.println(commonClassmateResult.getString("sname"));
        }
    }

    /**
     * (c)
     * <p>
     * Given a course name as a string on the command line, print to std the majors
     * of enrolled students and the count of students from each major, respectively.
     * Result should be sorted by major name. One major per row.
     *
     * @param className name of the class
     * @throws SQLException
     */
    private void getClassStatis(String className) throws SQLException {
        // Check if the class name is valid
        String checkClassQuery = "SELECT * FROM class WHERE name = ?";
        PreparedStatement checkClassStmt = connection.prepareStatement(checkClassQuery);
        checkClassStmt.setString(1, className);
        ResultSet classResult = checkClassStmt.executeQuery();
        if (!classResult.next()) {
            System.err.println(INVALID_CLASS);
            return;
        }

        // Get the major distribution
        String majorDistributionQuery = "SELECT\r\n" + //
                "    CASE\r\n" + //
                "        WHEN s.major IS NULL THEN 'TBD'\r\n" + //
                "        ELSE s.major\r\n" + //
                "    END AS major,\r\n" + //
                "    COUNT(*) AS count\r\n" + //
                "FROM\r\n" + //
                "    enrolled e\r\n" + //
                "    JOIN student s ON e.snum = s.snum\r\n" + //
                "WHERE\r\n" + //
                "    e.cname = ?\r\n" + //
                "GROUP BY\r\n" + //
                "    major\r\n" + //
                "ORDER BY\r\n" + //
                "    major";
        PreparedStatement majorDistributionStmt = connection.prepareStatement(majorDistributionQuery);
        majorDistributionStmt.setString(1, className);
        ResultSet majorDistributionResult = majorDistributionStmt.executeQuery();
        while (majorDistributionResult.next()) {
            String major = majorDistributionResult.getString("major");
            int count = majorDistributionResult.getInt("count");
            System.out.println(major + "\t" + count);
        }
    }
}
