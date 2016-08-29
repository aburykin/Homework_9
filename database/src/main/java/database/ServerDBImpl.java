package database;

import data.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABurykin on 24.08.2016.
 */
public class ServerDBImpl implements ServerDB {

    ArrayList<Employee> people;

    public ServerDBImpl() {
        this.people = new ArrayList();
        createData();
    }

    private void createData() {
        people.add(new Employee("Ваня", 43, true, 100000D));
        people.add(new Employee("Алена", 48, false, 10000D));
        people.add(new Employee("Гена", 19, true, 777D));
        people.add(new Employee("Гена", 19, true, 777D));
        people.add(new Employee("Гена", 19, true, 8976D));
        people.add(new Employee("Гена", 20, true, 777D));
        people.add(new Employee("Гарри", 19, true, 777D));
        people.add(new Employee("Катя", 19, false, 777D));
        people.add(new Employee("Максим", 31, true, 12000D));
        people.add(new Employee("Яна", 32, false, 5000D));
        people.add(new Employee("Олег", 36, true, 75000D));
        people.add(new Employee("Джуди", 23, false, 54231D));
        people.add(new Employee("Петрович", 73, true, 1000000D));

    }

    public List<Employee> runBackup() {
        System.out.println("Вызван настоящий метод runBackup класса ServerDBImpl");
        return people;
    }


    public List<Employee> getOnlyFemale() {
        System.out.println("Вызван настоящий метод getOnlyFemale класса ServerDBImpl");
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : people) {
            if (!employee.isGender()) result.add(employee);
        }
        return result;
    }

    public List<Employee> getOnlyMale() {
        System.out.println("Вызван настоящий метод getOnlyMale класса ServerDBImpl");
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : people) {
            if (employee.isGender()) result.add(employee);
        }
        return result;
    }

    public List<Employee> getOnlyRichEmployee(int salary) {
        System.out.println("Вызван настоящий метод getOnlyRichEmployee класса ServerDBImpl");
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : people) {
            if (employee.getSalary() >= (double) salary) result.add(employee);
        }
        return result;
    }

    public List<Employee> select(String name, int age, boolean gender, double salary) {
        System.out.println("Вызван настоящий метод select класса ServerDBImpl");
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : people) {
            if (employee.getName().equals(name)
                    && employee.getAge() == age
                    && employee.isGender() == gender
                    && employee.getSalary() == salary) {
                result.add(employee);
            }
        }
        return result;
    }


    public static void printResult(List<Employee> result) {
        if (result != null) {
            System.out.println("\nResult:");
            for (Employee employee : result) {
                System.out.println(employee);
            }
            System.out.println("\n");
        }

    }
}
