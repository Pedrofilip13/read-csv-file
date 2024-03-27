import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;

class Employee {
    private String name;
    private int age;
    private String department;
    private double salary;

    public Employee(String name, int age, String department, double salary) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }
}

public class EmployeeProcessor {
    public static void main(String[] args) throws IOException {
        List<Employee> employees = readEmployeeData("employees.csv");

        // Function to concatenate employee name and department
        Function<Employee, String> nameDeptConcatenator =
                employee -> employee.getName() + " - " + employee.getDepartment();

        // Generate a new collection with concatenated strings
        List<String> concatenatedStrings = employees.parallelStream()
                .map(nameDeptConcatenator)
                .collect(Collectors.toList());

        // Print concatenated strings
        concatenatedStrings.forEach(System.out::println);

        // Calculate average salary
        double averageSalary = employees.parallelStream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
        // Format average salary to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedAverageSalary = df.format(averageSalary);
        System.out.println("Average salary: " + formattedAverageSalary + "€");

        // Filter employees above age threshold
        int ageThreshold = 30;
        List<Employee> filteredEmployees = employees.parallelStream()
                .filter(employee -> employee.getAge() > ageThreshold)
                .collect(Collectors.toList());

        // Print filtered employees
        System.out.println("Employees above age " + ageThreshold + ":");
        filteredEmployees.forEach(employee ->
                System.out.println(employee.getName() + " - " + employee.getAge()));

        // Additional features
        calculateAdditionalStatistics(employees);
        sortEmployeesBySalary(employees);
    }

    // Read employee data from CSV file and store in a collection
    public static List<Employee> readEmployeeData(String filename) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Skip header line
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0].trim();
                int age = Integer.parseInt(data[1].trim());
                String department = data[2].trim();
                double salary = Double.parseDouble(data[3].trim());
                employees.add(new Employee(name, age, department, salary));
            }
        }
        return employees;
    }

    // Calculate additional statistics
    public static void calculateAdditionalStatistics(List<Employee> employees) {
        double maxSalary = employees.parallelStream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        System.out.println("Maximum salary: " + maxSalary + "€");

        int minAge = employees.parallelStream()
                .mapToInt(Employee::getAge)
                .min()
                .orElse(0);
        System.out.println("Minimum age: " + minAge);

        String mostCommonDepartment = employees.parallelStream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(e -> e.getValue()))
                .map(e -> e.getKey())
                .orElse("None");
        System.out.println("Most common department: " + mostCommonDepartment);
    }

    // Sort employees by salary
    public static void sortEmployeesBySalary(List<Employee> employees) {
        List<Employee> sortedBySalary = employees.parallelStream()
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .collect(Collectors.toList());
        System.out.println("Employees sorted by salary:");
        sortedBySalary.forEach(employee ->
                System.out.println(employee.getName() + " - " + employee.getSalary() + "€"));
    }
}

