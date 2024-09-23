package telran.employees;

import java.util.*;

public class CompanyImpl implements Company {
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    private class EmployeeIterator implements Iterator<Employee> {
        private Iterator<Employee> iterator;
        private Employee currentEmployee;

        public EmployeeIterator(Iterator<Employee> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Employee next() {
            currentEmployee = iterator.next();
            return currentEmployee;
        }

        @Override
        public void remove() {
            iterator.remove();
            removeEmployeesDepartment(currentEmployee);
            removeManagersFactor(currentEmployee);
        }
    }

    @Override
    public Iterator<Employee> iterator() {
        return new EmployeeIterator(employees.values().iterator());
    }

    @Override
    public void addEmployee(Employee empl) {
        long testId = empl.getId();
        if(employees.get(testId) != null) {
            throw new IllegalStateException();
        }
        employees.put(testId, empl);
        addEmployeesDepartment(empl);
        addManagersFactor(empl);
    }

    private void addEmployeesDepartment(Employee empl) {
        String department = empl.getDepartment();
        employeesDepartment.computeIfAbsent(department, k -> new ArrayList<Employee>()).add(empl);
    }

    private void addManagersFactor(Employee empl) {
        if(empl instanceof Manager) {
            Manager newEmpl = (Manager) empl;
            managersFactor.computeIfAbsent(newEmpl.getFactor(), k -> new ArrayList<Manager>()).add(newEmpl);
        }
    }

    @Override
    public Employee getEmployee(long id) {
        return employees.get(id);
    }

    @Override
    public Employee removeEmployee(long id) {
        Employee empl = employees.get(id);
        if(empl == null) {
            throw new NoSuchElementException();
        }
        removeEmployeesDepartment(empl);
        removeManagersFactor(empl);
        return employees.remove(id);
    }

    private void removeManagersFactor(Employee empl) {
        if(empl instanceof Manager) {
            Manager newEmpl = (Manager) empl;
            Float factor = newEmpl.getFactor();
            List<Manager> list = managersFactor.get(factor);
            list.remove(newEmpl);
            if(list.size() == 0) {
                managersFactor.remove(factor);
            }
        }
    }

    private void removeEmployeesDepartment(Employee empl) {
        String department = empl.getDepartment();
        List<Employee> list = employeesDepartment.get(department);
        list.remove(empl);
        if(list.size() == 0) {
            employeesDepartment.remove(department);
        }
    } 

    @Override
    public int getDepartmentBudget(String department) {
        int result = 0;
        List<Employee> list = employeesDepartment.get(department);
            if(list != null) {
                for (Employee empl : list) {
                    result += empl.computeSalary();
                }
            }
        return result;
    }

    @Override
    public String[] getDepartments() {
        String[] result = employeesDepartment.keySet().toArray(new String[0]);
        Arrays.sort(result);
        return result; 
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Map.Entry<Float, List<Manager>> lastEntry = managersFactor.lastEntry();
        Manager[] result = lastEntry != null ? lastEntry.getValue().toArray(new Manager[0]) : new Manager[0];
        return result;
    }

}
