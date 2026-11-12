import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Loads student records from a text file into a plain {@code Student[]} array,
 * computes class statistics, searches by ID, and writes a formatted report.
 *
 * <p>Only a 1D array is used for storage. When the array fills up it is grown
 * by hand (allocate a bigger array, copy with a loop, swap the reference).
 * Sorting and searching are implemented from scratch: Selection Sort and
 * Linear Search.</p>
 *
 * @author Adrian
 * @version 1.0
 */
public class GradebookAnalyzer {

    /** Starting size of the student array. Deliberately small so resizing is exercised. */
    public static final int INITIAL_CAPACITY = 10;

    /** Labels for the five statistic columns, in the order produced by {@link #extractColumn(int)}. */
    public static final String[] COLUMN_NAMES =
            {"Exam 1", "Exam 2", "Exam 3", "Final Exam", "Course Avg"};

    /** Number of comma-separated fields expected on every input line. */
    private static final int FIELDS_PER_LINE = 7;

    /** Width of the divider lines in the report. */
    private static final int REPORT_WIDTH = 79;

    /** Backing storage; only the first {@code studentCount} slots are in use. */
    private Student[] students;

    /** Number of valid students currently stored. */
    private int studentCount;

    /** Number of non-blank input lines rejected during loading. */
    private int skippedLines;

    /**
     * Creates an empty analyzer with an array of {@link #INITIAL_CAPACITY} slots.
     */
    public GradebookAnalyzer() {
        students = new Student[INITIAL_CAPACITY];
        studentCount = 0;
        skippedLines = 0;
    }

    /**
     * Gets the number of valid students loaded.
     *
     * @return the student count
     */
    public int getStudentCount() {
        return studentCount;
    }

    /**
     * Gets the number of input lines that were rejected as malformed.
     *
     * @return the skipped-line count
     */
    public int getSkippedLines() {
        return skippedLines;
    }

    /**
     * Reads every record from the given file. Blank lines are ignored, and
     * malformed lines are reported and skipped instead of stopping the run.
     * Expected line format:
     * {@code StudentID,LastName,FirstName,Exam1,Exam2,Exam3,FinalExam}
     *
     * @param filename path of the input file
     * @return {@code true} if the file could be opened and read, {@code false}
     *         if it was not found
     */
    public boolean loadFromFile(String filename) {
        students = new Student[INITIAL_CAPACITY];
        studentCount = 0;
        skippedLines = 0;

        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(filename));
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // blank lines are not errors
                }
                parseRecord(line, lineNumber);
            }
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Could not open \"" + filename
                    + "\". Check that the file exists in the current folder.");
            return false;
        } finally {
            // Runs whether or not an exception occurred, so the file handle is always released.
            if (fileScanner != null) {
                fileScanner.close();
            }
        }
    }

    /**
     * Validates one input line and, if it is good, stores the student.
     * Every failure path ends in {@link #reject(int, String)}.
     *
     * @param line       the raw text line
     * @param lineNumber 1-based line number, used in warning messages
     */
    private void parseRecord(String line, int lineNumber) {
        // The -1 limit keeps trailing empty fields so "a,b,,," is counted correctly.
        String[] parts = line.split(",", -1);
        if (parts.length != FIELDS_PER_LINE) {
            reject(lineNumber, "expected " + FIELDS_PER_LINE
                    + " comma-separated fields but found " + parts.length);
            return;
        }

        String id = parts[0].trim();
        if (linearSearchById(id) != -1) {
            reject(lineNumber, "duplicate student ID " + id);
            return;
        }

        try {
            double e1 = Double.parseDouble(parts[3].trim());
            double e2 = Double.parseDouble(parts[4].trim());
            double e3 = Double.parseDouble(parts[5].trim());
            double fin = Double.parseDouble(parts[6].trim());
            // The Student constructor validates names and the 0-100 range.
            addStudent(new Student(id, parts[1], parts[2], e1, e2, e3, fin));
        } catch (NumberFormatException ex) {
            // Must be caught before IllegalArgumentException, its superclass.
            reject(lineNumber, "score is not a valid number (" + ex.getMessage() + ")");
        } catch (IllegalArgumentException ex) {
            reject(lineNumber, ex.getMessage());
        }
    }

    /**
     * Prints a warning for a rejected line and counts it.
     *
     * @param lineNumber 1-based line number in the input file
     * @param reason     short explanation of what was wrong
     */
    private void reject(int lineNumber, String reason) {
        System.out.println("  [WARNING] Line " + lineNumber + " skipped: " + reason);
        skippedLines++;
    }

    /**
     * Appends a student, growing the array first if it is full.
     *
     * @param s the student to store
     */
    private void addStudent(Student s) {
        if (studentCount == students.length) {
            resizeArray();
        }
        students[studentCount] = s;
        studentCount++;
    }

    /**
     * Doubles the capacity of the array without using any library helper.
     * A new array is allocated, each element reference is copied in a loop,
     * and the field is repointed at the new array. The old array becomes
     * unreachable and is garbage collected.
     */
    private void resizeArray() {
        int newCapacity = students.length * 2;
        Student[] bigger = new Student[newCapacity];
        for (int i = 0; i < studentCount; i++) {
            bigger[i] = students[i];
        }
        System.out.println("  [INFO] Array full: capacity grown from "
                + students.length + " to " + newCapacity + ".");
        students = bigger;
    }

    /**
     * Linear search for a student ID (case-insensitive). Checks each slot in
     * order and stops at the first match, so the array does not need to be sorted.
     *
     * @param id the ID to find
     * @return the array index of the match, or -1 if not found
     */
    public int linearSearchById(String id) {
        if (id == null) {
            return -1;
        }
        String target = id.trim();
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Looks up a student by ID.
     *
     * @param id the ID to find
     * @return the matching {@code Student}, or {@code null} if there is none
     */
    public Student findStudentById(String id) {
        int index = linearSearchById(id);
        if (index == -1) {
            return null;
        }
        return students[index];
    }

    /**
     * Selection Sort (ascending) on the first {@code n} values of an array.
     * The array is sorted <em>in place</em>: the parameter holds a copy of the
     * caller's reference, so both point at the same array object.
     *
     * @param data the values to sort
     * @param n    how many leading elements to sort
     */
    public static void selectionSort(double[] data, int n) {
        for (int i = 0; i < n - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < n; j++) {
                if (data[j] < data[smallest]) {
                    smallest = j;
                }
            }
            if (smallest != i) {
                double temp = data[i];
                data[i] = data[smallest];
                data[smallest] = temp;
            }
        }
    }

    /**
     * Selection Sort of students by weighted average, highest first.
     * Students with equal averages may end up in either order because
     * Selection Sort is not stable.
     *
     * @param arr the students to sort (rearranged in place)
     * @param n   how many leading elements to sort
     */
    public static void selectionSortByAverageDescending(Student[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j].getWeightedAverage() > arr[best].getWeightedAverage()) {
                    best = j;
                }
            }
            if (best != i) {
                Student temp = arr[i];
                arr[i] = arr[best];
                arr[best] = temp;
            }
        }
    }

    /**
     * Copies one score column (one value per student) into a fresh array.
     *
     * @param column 0 = Exam 1, 1 = Exam 2, 2 = Exam 3, 3 = Final Exam, 4 = course average
     * @return an array of length {@code studentCount}
     */
    private double[] extractColumn(int column) {
        double[] values = new double[studentCount];
        for (int i = 0; i < studentCount; i++) {
            Student s = students[i];
            switch (column) {
                case 0:
                    values[i] = s.getExam1();
                    break;
                case 1:
                    values[i] = s.getExam2();
                    break;
                case 2:
                    values[i] = s.getExam3();
                    break;
                case 3:
                    values[i] = s.getFinalExam();
                    break;
                default:
                    values[i] = s.getWeightedAverage();
                    break;
            }
        }
        return values;
    }

    /**
     * Arithmetic mean of the first {@code n} values.
     *
     * @param data the values
     * @param n    how many values to use (must be at least 1)
     * @return the mean
     */
    public static double mean(double[] data, int n) {
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += data[i];
        }
        return sum / n;
    }

    /**
     * Median of the first {@code n} values. Works on a copy so the caller's
     * array keeps its original order. With an even count the median is the
     * average of the two middle values, so ties in the middle simply return
     * that shared value.
     *
     * @param data the values
     * @param n    how many values to use (must be at least 1)
     * @return the median
     */
    public static double median(double[] data, int n) {
        double[] copy = new double[n];
        for (int i = 0; i < n; i++) {
            copy[i] = data[i];
        }
        selectionSort(copy, n); // sorts the copy only; data is untouched
        if (n % 2 == 1) {
            return copy[n / 2];
        }
        return (copy[n / 2 - 1] + copy[n / 2]) / 2.0;
    }

    /**
     * Population standard deviation of the first {@code n} values. The class
     * roster is the entire population (not a sample), so the divisor is N.
     * A single value gives 0.0.
     *
     * @param data the values
     * @param n    how many values to use (must be at least 1)
     * @return the population standard deviation
     */
    public static double standardDeviation(double[] data, int n) {
        double avg = mean(data, n);
        double sumSquares = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = data[i] - avg;
            sumSquares += diff * diff;
        }
        return Math.sqrt(sumSquares / n);
    }

    /**
     * Largest of the first {@code n} values.
     *
     * @param data the values
     * @param n    how many values to use (must be at least 1)
     * @return the maximum
     */
    public static double highest(double[] data, int n) {
        double max = data[0];
        for (int i = 1; i < n; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    /**
     * Smallest of the first {@code n} values.
     *
     * @param data the values
     * @param n    how many values to use (must be at least 1)
     * @return the minimum
     */
    public static double lowest(double[] data, int n) {
        double min = data[0];
        for (int i = 1; i < n; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    /**
     * Prints every student in input order as an aligned table.
     *
     * @param out the destination (console or file writer)
     */
    public void printRoster(PrintWriter out) {
        out.println("STUDENT ROSTER (input order)");
        out.println("-".repeat(REPORT_WIDTH));
        out.printf("%-8s %-12s %-10s %7s %7s %7s %7s %8s %5s%n",
                "ID", "Last", "First", "Exam1", "Exam2", "Exam3", "Final", "Average", "Grade");
        out.println("-".repeat(REPORT_WIDTH));
        for (int i = 0; i < studentCount; i++) {
            Student s = students[i];
            out.printf("%-8s %-12s %-10s %7.2f %7.2f %7.2f %7.2f %8.2f %5c%n",
                    s.getStudentId(), s.getLastName(), s.getFirstName(),
                    s.getExam1(), s.getExam2(), s.getExam3(), s.getFinalExam(),
                    s.getWeightedAverage(), s.getLetterGrade());
        }
    }

    /**
     * Prints class-wide statistics for each exam and the course average, the
     * letter-grade distribution, and the top and bottom performers.
     *
     * @param out the destination (console or file writer)
     */
    public void printStatistics(PrintWriter out) {
        if (studentCount == 0) {
            out.println("No student records loaded; nothing to analyze.");
            return;
        }

        out.println("CLASS STATISTICS (N = " + studentCount
                + ", population standard deviation)");
        out.println("-".repeat(57));
        out.printf("%-12s %8s %8s %8s %8s %8s%n",
                "Component", "Mean", "Median", "Std Dev", "Highest", "Lowest");
        out.println("-".repeat(57));
        for (int col = 0; col < COLUMN_NAMES.length; col++) {
            double[] data = extractColumn(col);
            out.printf("%-12s %8.2f %8.2f %8.2f %8.2f %8.2f%n",
                    COLUMN_NAMES[col],
                    mean(data, studentCount),
                    median(data, studentCount),
                    standardDeviation(data, studentCount),
                    highest(data, studentCount),
                    lowest(data, studentCount));
        }

        // Letter grade distribution
        out.println();
        out.println("LETTER GRADE DISTRIBUTION");
        out.println("-".repeat(57));
        String letters = "ABCDF";
        int[] counts = new int[letters.length()];
        for (int i = 0; i < studentCount; i++) {
            counts[letters.indexOf(students[i].getLetterGrade())]++;
        }
        for (int k = 0; k < letters.length(); k++) {
            out.printf("  %c : %2d  (%5.1f%%)%n",
                    letters.charAt(k), counts[k], 100.0 * counts[k] / studentCount);
        }

        // Rank a copy of the reference array so the original input order is preserved.
        Student[] ranked = new Student[studentCount];
        for (int i = 0; i < studentCount; i++) {
            ranked[i] = students[i];
        }
        selectionSortByAverageDescending(ranked, studentCount);

        int k = Math.min(3, studentCount);
        out.println();
        out.println("TOP " + k + " PERFORMER(S)");
        out.println("-".repeat(57));
        for (int i = 0; i < k; i++) {
            Student s = ranked[i];
            out.printf("  %d. %-24s %7.2f  %c%n",
                    i + 1, s.getFullName(), s.getWeightedAverage(), s.getLetterGrade());
        }

        out.println();
        out.println("BOTTOM " + k + " PERFORMER(S) (lowest first)");
        out.println("-".repeat(57));
        for (int i = 0; i < k; i++) {
            Student s = ranked[studentCount - 1 - i];
            out.printf("  %d. %-24s %7.2f  %c%n",
                    i + 1, s.getFullName(), s.getWeightedAverage(), s.getLetterGrade());
        }
    }

    /**
     * Prints the complete report: header, roster, and statistics.
     *
     * @param out the destination (console or file writer)
     */
    public void printReport(PrintWriter out) {
        out.println("=".repeat(REPORT_WIDTH));
        out.println("  STUDENT GRADEBOOK & STATISTICAL ANALYZER - CLASS REPORT");
        out.println("=".repeat(REPORT_WIDTH));
        out.printf("Records analyzed       : %d%n", studentCount);
        out.printf("Malformed lines skipped: %d%n", skippedLines);
        out.printf("Weighting              : Exams 1-3 = %.0f%% each, Final Exam = %.0f%%%n",
                Student.EXAM_WEIGHT * 100, Student.FINAL_WEIGHT * 100);
        out.println();
        printRoster(out);
        out.println();
        printStatistics(out);
    }

    /**
     * Writes the full report to a file.
     *
     * @param filename the output file name
     * @return {@code true} if the file was written without error, {@code false} otherwise
     */
    public boolean exportReport(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename);
            printReport(writer);
            writer.flush();
            return !writer.checkError();
        } catch (FileNotFoundException ex) {
            // Thrown when the file cannot be created (bad path, no permission).
            System.out.println("ERROR: Could not create \"" + filename + "\".");
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
