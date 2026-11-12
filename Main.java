import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Console front end for the Student Gradebook &amp; Statistical Analyzer.
 * Loads {@code grades_input.txt}, then runs a menu loop until the user exits.
 *
 * @author Adrian
 * @version 1.0
 */
public class Main {

    /** Input file, read from the current working directory. */
    private static final String INPUT_FILE = "grades_input.txt";

    /** Report file written by the export option. */
    private static final String REPORT_FILE = "gradebook_report.txt";

    /**
     * Static counter for the scope demo. Because it belongs to the class, it
     * keeps its value between method calls (persistence).
     */
    private static int demoCallCount = 0;

    /**
     * Program entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Student Gradebook & Statistical Analyzer ===");
        GradebookAnalyzer analyzer = new GradebookAnalyzer();

        if (!analyzer.loadFromFile(INPUT_FILE)) {
            System.out.println("Cannot continue without input data. Put "
                    + INPUT_FILE + " in the working directory and run again.");
            return;
        }
        if (analyzer.getStudentCount() == 0) {
            System.out.println("No valid student records were found in "
                    + INPUT_FILE + ". Nothing to analyze.");
            return;
        }
        System.out.printf("Loaded %d record(s); skipped %d malformed line(s).%n",
                analyzer.getStudentCount(), analyzer.getSkippedLines());

        Scanner keyboard = new Scanner(System.in);
        PrintWriter console = new PrintWriter(System.out);
        boolean running = true;

        while (running) {
            printMenu();
            String choice = readLine(keyboard);

            if (choice == null) { // input ended (Ctrl+D, or a piped test script ran out)
                System.out.println("\nNo more input. Goodbye.");
                break;
            }

            switch (choice) {
                case "1":
                    analyzer.printStatistics(console);
                    console.flush();
                    break;
                case "2":
                    analyzer.printRoster(console);
                    console.flush();
                    break;
                case "3":
                    searchForStudent(analyzer, keyboard);
                    break;
                case "4":
                    if (analyzer.exportReport(REPORT_FILE)) {
                        System.out.println("Report written to " + REPORT_FILE);
                    } else {
                        System.out.println("Report could not be written.");
                    }
                    break;
                case "5":
                    runScopeDemo();
                    break;
                case "0":
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice \"" + choice + "\". Enter a number from 0 to 5.");
                    break;
            }
        }
        keyboard.close();
    }

    /**
     * Prints the menu.
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("------------- MENU -------------");
        System.out.println("1) View class statistics and rankings");
        System.out.println("2) View student roster");
        System.out.println("3) Search for a student by ID");
        System.out.println("4) Export report to " + REPORT_FILE);
        System.out.println("5) Scope and pass-by-value demonstration");
        System.out.println("0) Exit");
        System.out.print("Choice: ");
    }

    /**
     * Reads one line of input, or returns {@code null} when there is no more input.
     *
     * @param keyboard the console scanner
     * @return the trimmed line, or {@code null} at end of input
     */
    private static String readLine(Scanner keyboard) {
        if (!keyboard.hasNextLine()) {
            return null;
        }
        return keyboard.nextLine().trim();
    }

    /**
     * Prompts for a student ID and displays the match, if any.
     *
     * @param analyzer the loaded gradebook
     * @param keyboard the console scanner
     */
    private static void searchForStudent(GradebookAnalyzer analyzer, Scanner keyboard) {
        System.out.print("Enter student ID: ");
        String id = readLine(keyboard);
        if (id == null || id.isEmpty()) {
            System.out.println("No ID entered.");
            return;
        }
        Student found = analyzer.findStudentById(id);
        if (found == null) {
            System.out.println("No student with ID \"" + id + "\" was found.");
        } else {
            System.out.println("Found: " + found);
        }
    }

    /**
     * Receives a <em>copy</em> of a primitive. Changing the parameter changes
     * only this method's local copy.
     *
     * @param score a copy of the caller's value
     */
    private static void tryToChangePrimitive(double score) {
        score = 100.0;
    }

    /**
     * Receives a copy of an object <em>reference</em>. Both the copy and the
     * caller's variable point at the same object, so changes made through the
     * reference are visible to the caller.
     *
     * @param s a copy of the caller's reference
     */
    private static void changeObjectThroughReference(Student s) {
        s.setFinalExam(100.0);
    }

    /**
     * Reassigns the parameter to a brand-new object. Only this method's copy
     * of the reference is repointed; the caller's variable is unaffected.
     *
     * @param s a copy of the caller's reference
     */
    private static void reassignReference(Student s) {
        s = new Student("TEMP", "Temp", "Temp", 0, 0, 0, 0);
    }

    /**
     * Shows a local variable (recreated on every call) next to a static
     * variable (kept for the life of the program).
     */
    private static void showScopeAndPersistence() {
        int localCalls = 0; // method scope: born and destroyed with each call
        localCalls++;
        demoCallCount++;    // class scope: survives between calls
        System.out.println("  local counter = " + localCalls
                + ", static counter = " + demoCallCount);
    }

    /**
     * Runs the scope and pass-by-value demonstration using a throwaway Student so the real
     * gradebook data is never modified.
     */
    private static void runScopeDemo() {
        System.out.println();
        System.out.println("--- Pass-by-value with a primitive ---");
        double score = 70.0;
        tryToChangePrimitive(score);
        System.out.println("  After tryToChangePrimitive: score = " + score
                + "  (unchanged: the method got a copy)");

        System.out.println("--- Pass-by-value with an object reference ---");
        Student demo = new Student("DEMO", "Demo", "Student", 80, 80, 80, 80);
        System.out.printf("  Before: final exam = %.1f, average = %.2f%n",
                demo.getFinalExam(), demo.getWeightedAverage());
        changeObjectThroughReference(demo);
        System.out.printf("  After changeObjectThroughReference: final exam = %.1f, average = %.2f"
                + "  (changed: same object)%n", demo.getFinalExam(), demo.getWeightedAverage());
        reassignReference(demo);
        System.out.println("  After reassignReference: last name = " + demo.getLastName()
                + "  (unchanged: only the copy was repointed)");

        System.out.println("--- Scope and persistence ---");
        showScopeAndPersistence();
        showScopeAndPersistence();
        System.out.println("  Student objects constructed this run: " + Student.getTotalCreated());

        for (int i = 0; i < 1; i++) {
            int blockScoped = i; // visible only inside this loop body
            System.out.println("  Loop-scoped variable inside block = " + blockScoped);
        }
        // Using 'blockScoped' or 'i' here would be a compile error: they are out of scope.
    }
}
