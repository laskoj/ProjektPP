import java.sql.*;
import java.util.*;

// Main class do uruchomienia programu
public class Main {
    public static void main(String[] args) {
        // Start the GUI APP
        javax.swing.SwingUtilities.invokeLater(() -> {
            StudentManagementGUI gui = new StudentManagementGUI();
            gui.createAndShowGUI();
        });
    }
}

// Student class
class Student {
    private String name;
    private int age;
    private double grade;
    private String studentID;

    // Konstruktor
    public Student(String name, int age, double grade, String studentID) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.studentID = studentID;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    // Method: wyświetlanie informacji o studencie
    public void displayInfo() {
        System.out.println("Student ID: " + studentID);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Grade: " + grade);
    }
}

// Interface dla StudentManager
interface StudentManager {
    void addStudent(Student student);
    void removeStudent(String studentID);
    void updateStudent(String studentID, Student updatedStudent);
    List<Student> displayAllStudents();
    double calculateAverageGrade();
}

// Implementacja dla interfejsu StudentManager
class StudentManagerImpl implements StudentManager {
    private static final String DB_URL = "jdbc:sqlite:students.db";

    public StudentManagerImpl() {
        createTableIfNotExists();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students ("
                + "studentID TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "age INTEGER,"
                + "grade REAL"
                + ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addStudent(Student student) {
        String insertSQL = "INSERT INTO students (studentID, name, age, grade) VALUES (?, ?, ?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, student.getStudentID());
            pstmt.setString(2, student.getName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeStudent(String studentID) {
        String deleteSQL = "DELETE FROM students WHERE studentID = ?;";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, studentID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStudent(String studentID, Student updatedStudent) {
        String updateSQL = "UPDATE students SET name = ?, age = ?, grade = ? WHERE studentID = ?;";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, updatedStudent.getName());
            pstmt.setInt(2, updatedStudent.getAge());
            pstmt.setDouble(3, updatedStudent.getGrade());
            pstmt.setString(4, studentID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Student> displayAllStudents() {
        List<Student> students = new ArrayList<>();
        String selectSQL = "SELECT * FROM students;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                String id = rs.getString("studentID");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                double grade = rs.getDouble("grade");
                students.add(new Student(name, age, grade, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public double calculateAverageGrade() {
        String avgSQL = "SELECT AVG(grade) AS avgGrade FROM students;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(avgSQL)) {
            if (rs.next()) {
                return rs.getDouble("avgGrade");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

// GUI Class
class StudentManagementGUI {
    private javax.swing.JFrame frame;
    private StudentManager manager;

    public StudentManagementGUI() {
        manager = new StudentManagerImpl();
    }

    public void createAndShowGUI() {
        frame = new javax.swing.JFrame("Student Management System");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new java.awt.BorderLayout());

        // Dodanie komponentów (panel, button)
        javax.swing.JPanel inputPanel = new javax.swing.JPanel(new java.awt.GridLayout(5, 2));
        inputPanel.add(new javax.swing.JLabel("Student ID:"));
        javax.swing.JTextField idField = new javax.swing.JTextField();
        inputPanel.add(idField);

        inputPanel.add(new javax.swing.JLabel("Name:"));
        javax.swing.JTextField nameField = new javax.swing.JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new javax.swing.JLabel("Age:"));
        javax.swing.JTextField ageField = new javax.swing.JTextField();
        inputPanel.add(ageField);

        inputPanel.add(new javax.swing.JLabel("Grade:"));
        javax.swing.JTextField gradeField = new javax.swing.JTextField();
        inputPanel.add(gradeField);

        javax.swing.JButton addButton = new javax.swing.JButton("Add Student");
        inputPanel.add(addButton);

        javax.swing.JTextArea outputArea = new javax.swing.JTextArea();
        outputArea.setEditable(false);
        frame.add(inputPanel, java.awt.BorderLayout.NORTH);
        frame.add(new javax.swing.JScrollPane(outputArea), java.awt.BorderLayout.CENTER);

        // Dodanie akcji listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                double grade = Double.parseDouble(gradeField.getText());
                Student student = new Student(name, age, grade, id);
                manager.addStudent(student);
                outputArea.append("Student added successfully!\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input: " + ex.getMessage() + "\n");
            }
        });

        frame.setVisible(true);
    }
}
