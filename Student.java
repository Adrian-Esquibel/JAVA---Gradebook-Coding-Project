/**
 * Models one student's record in the gradebook.
 *
 * <p>This class demonstrates data abstraction: every field is {@code private},
 * and all access goes through constructors, getters, and validating setters so
 * an object can never hold an out-of-range score or a blank name.</p>
 *
 * <p>Grading policy: Exams 1-3 are worth 20% each and the Final Exam is worth
 * 40%. The weighted average is rounded to two decimal places before the letter
 * grade is assigned.</p>
 *
 * @author Adrian
 * @version 1.0
 */
public final class Student { // final: setters run inside the constructor, so no subclass may override them

    /** Weight of each of the three exams (20% each). */
    public static final double EXAM_WEIGHT = 0.20;

    /** Weight of the final exam (40%). */
    public static final double FINAL_WEIGHT = 0.40;

    /** Lowest score accepted for any exam. */
    public static final double MIN_SCORE = 0.0;

    /** Highest score accepted for any exam. */
    public static final double MAX_SCORE = 100.0;

    /**
     * Class-level (static) counter shared by every Student object. It persists
     * for the whole program run, unlike instance fields (which live as long as
     * their object) or local variables (which live only for one method call).
     */
    private static int totalCreated = 0;

    /** Unique identifier, for example "1004". */
    private String studentId;

    /** Family name. */
    private String lastName;

    /** Given name. */
    private String firstName;

    /** Score on Exam 1 (0-100). */
    private double exam1;

    /** Score on Exam 2 (0-100). */
    private double exam2;

    /** Score on Exam 3 (0-100). */
    private double exam3;

    /** Score on the Final Exam (0-100). */
    private double finalExam;

    /**
     * Creates a student after validating every argument.
     *
     * @param studentId unique ID; must not be null or blank
     * @param lastName  family name; must not be null or blank
     * @param firstName given name; must not be null or blank
     * @param exam1     Exam 1 score, 0-100
     * @param exam2     Exam 2 score, 0-100
     * @param exam3     Exam 3 score, 0-100
     * @param finalExam Final Exam score, 0-100
     * @throws IllegalArgumentException if any text is blank or any score is outside 0-100 or NaN
     */
    public Student(String studentId, String lastName, String firstName,
                   double exam1, double exam2, double exam3, double finalExam) {
        setStudentId(studentId);
        setLastName(lastName);
        setFirstName(firstName);
        setExam1(exam1);
        setExam2(exam2);
        setExam3(exam3);
        setFinalExam(finalExam);
        totalCreated++;
    }

    /**
     * Validates a text field.
     *
     * @param label name of the field, used in the error message
     * @param value the text to check
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is null or blank
     */
    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value.trim();
    }

    /**
     * Validates a score. The comparison is written so that NaN also fails,
     * because every comparison with NaN is false.
     *
     * @param label name of the score, used in the error message
     * @param score the value to check
     * @return the same score if it is valid
     * @throws IllegalArgumentException if the score is not within 0-100
     */
    private static double requireValidScore(String label, double score) {
        if (!(score >= MIN_SCORE && score <= MAX_SCORE)) {
            throw new IllegalArgumentException(
                    label + " must be between 0 and 100 (got " + score + ")");
        }
        return score;
    }

    /**
     * Returns the number of Student objects constructed so far in this run.
     *
     * @return the static construction counter
     */
    public static int getTotalCreated() {
        return totalCreated;
    }

    /**
     * Converts a numeric average to a letter grade.
     * A: 90 and above, B: 80 up to 90, C: 70 up to 80, D: 60 up to 70, F: below 60.
     *
     * @param average a weighted average on the 0-100 scale
     * @return 'A', 'B', 'C', 'D', or 'F'
     */
    public static char letterGradeFor(double average) {
        if (average >= 90.0) {
            return 'A';
        } else if (average >= 80.0) {
            return 'B';
        } else if (average >= 70.0) {
            return 'C';
        } else if (average >= 60.0) {
            return 'D';
        }
        return 'F';
    }

    /**
     * Computes the weighted course average: 20% for each of Exams 1-3 plus 40%
     * for the final. The result is rounded to two decimals so floating-point
     * noise (for example 69.99999999999999) cannot push a 70.00 into a D.
     *
     * @return the weighted average, rounded to two decimal places
     */
    public double getWeightedAverage() {
        double raw = EXAM_WEIGHT * (exam1 + exam2 + exam3) + FINAL_WEIGHT * finalExam;
        return Math.round(raw * 100.0) / 100.0;
    }

    /**
     * Returns this student's letter grade based on the weighted average.
     *
     * @return 'A', 'B', 'C', 'D', or 'F'
     */
    public char getLetterGrade() {
        return letterGradeFor(getWeightedAverage());
    }

    /**
     * Returns the name formatted as "Last, First".
     *
     * @return the display name
     */
    public String getFullName() {
        return lastName + ", " + firstName;
    }

    /**
     * Gets the student ID.
     *
     * @return the ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId the new ID; must not be blank
     * @throws IllegalArgumentException if blank
     */
    public void setStudentId(String studentId) {
        this.studentId = requireText("Student ID", studentId);
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name; must not be blank
     * @throws IllegalArgumentException if blank
     */
    public void setLastName(String lastName) {
        this.lastName = requireText("Last name", lastName);
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name; must not be blank
     * @throws IllegalArgumentException if blank
     */
    public void setFirstName(String firstName) {
        this.firstName = requireText("First name", firstName);
    }

    /**
     * Gets the Exam 1 score.
     *
     * @return Exam 1 score
     */
    public double getExam1() {
        return exam1;
    }

    /**
     * Sets the Exam 1 score.
     *
     * @param exam1 score from 0 to 100
     * @throws IllegalArgumentException if out of range
     */
    public void setExam1(double exam1) {
        this.exam1 = requireValidScore("Exam 1", exam1);
    }

    /**
     * Gets the Exam 2 score.
     *
     * @return Exam 2 score
     */
    public double getExam2() {
        return exam2;
    }

    /**
     * Sets the Exam 2 score.
     *
     * @param exam2 score from 0 to 100
     * @throws IllegalArgumentException if out of range
     */
    public void setExam2(double exam2) {
        this.exam2 = requireValidScore("Exam 2", exam2);
    }

    /**
     * Gets the Exam 3 score.
     *
     * @return Exam 3 score
     */
    public double getExam3() {
        return exam3;
    }

    /**
     * Sets the Exam 3 score.
     *
     * @param exam3 score from 0 to 100
     * @throws IllegalArgumentException if out of range
     */
    public void setExam3(double exam3) {
        this.exam3 = requireValidScore("Exam 3", exam3);
    }

    /**
     * Gets the Final Exam score.
     *
     * @return Final Exam score
     */
    public double getFinalExam() {
        return finalExam;
    }

    /**
     * Sets the Final Exam score.
     *
     * @param finalExam score from 0 to 100
     * @throws IllegalArgumentException if out of range
     */
    public void setFinalExam(double finalExam) {
        this.finalExam = requireValidScore("Final Exam", finalExam);
    }

    /**
     * Builds a one-line description of this student.
     *
     * @return ID, name, all four scores, weighted average, and letter grade
     */
    @Override
    public String toString() {
        return String.format(
                "%s | %s | Exams: %.2f %.2f %.2f | Final: %.2f | Avg: %.2f (%c)",
                studentId, getFullName(), exam1, exam2, exam3, finalExam,
                getWeightedAverage(), getLetterGrade());
    }
}
