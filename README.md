# JAVA---Gradebook-Project

# Student Gradebook & Statistical Analyzer
 
A console application written in pure Java that reads student exam scores from a text file, computes weighted averages and letter grades, produces class-wide statistics, and exports a formatted report. It was built as a portfolio project for the EVC COMSC 075 (Computer Science I) prerequisite challenge for COMSC 076.
 
The project deliberately sticks to Computer Science I material: a custom class, a plain `Student[]` array that is resized by hand, sorting and searching written from scratch, and sequential file I/O with exception handling. There is no `ArrayList`, no `HashMap`, no streams, and no `Arrays.sort()`.
 
## Features
 
- Reads `grades_input.txt` and validates every line (field count, numeric scores, 0-100 range, duplicate IDs). Bad lines are reported and skipped instead of crashing the program.
- Weighted course average: Exams 1-3 at 20% each, Final Exam at 40%. Letter grades A/B/C/D/F at 90/80/70/60.
- Class statistics for Exam 1, Exam 2, Exam 3, Final Exam, and the course average: mean, median, standard deviation, highest, lowest.
- Letter-grade distribution plus top 3 and bottom 3 performers.
- Search for a student by ID.
- Interactive menu, plus export of the full report to `gradebook_report.txt`.
- A built-in demonstration of variable scope, persistence, and pass-by-value behavior.

## Project structure
 
```
.
├── Student.java            # Data class: private fields, validation, weighted average, letter grade
├── GradebookAnalyzer.java  # File loading, array resizing, search, selection sort, statistics, report
├── Main.java               # Console menu and the scope / pass-by-value demonstration
├── grades_input.txt        # Sample data (12 students, including boundary cases)
└── README.md
```
