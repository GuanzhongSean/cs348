import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;

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
    }
}
