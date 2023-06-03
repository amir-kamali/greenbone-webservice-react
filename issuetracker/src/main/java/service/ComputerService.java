package service;

import org.springframework.stereotype.Service;

import model.Computer;
import model.Employee;
import repository.ComputerRepository;
import repository.EmployeeRepository;

import java.util.List;

@Service
public class ComputerService {

    private final ComputerRepository computerRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;

    public ComputerService(ComputerRepository computerRepository,
            EmployeeRepository employeeRepository,
            EmployeeService employeeService,
            NotificationService notificationService) {
        this.computerRepository = computerRepository;
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    public Computer saveComputer(Computer computer) {
        return computerRepository.save(computer);
    }

    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }

    public Computer getComputerById(Long id) {
        return computerRepository.findById(id).orElseThrow();
    }

    public void deleteComputer(Long id) {
        Computer computer = getComputerById(id);
        Employee employee = computer.getEmployee();
        if (employee != null) {
            employee.removeComputer(computer);
            employeeService.saveEmployee(employee);
        }
        computerRepository.delete(computer);
    }

    public Employee getEmployeeByAbbreviation(String abbreviation) {
        Employee employee = employeeRepository.findByAbbreviation(abbreviation);
        return employee;
    }

    public Computer assignComputerToEmployee(Long computerId, String employeeAbbreviation) {
        Computer computer = getComputerById(computerId);
        Employee employee = getEmployeeByAbbreviation(employeeAbbreviation);
        employee.addComputer(computer);
        if (employee.getComputers().size() >= 3) {
            notificationService.sendNotification(employeeAbbreviation);
        }
        employeeService.saveEmployee(employee);
        return computer;
    }

    public Computer unassignComputerFromEmployee(Long computerId) {
        Computer computer = getComputerById(computerId);
        Employee employee = computer.getEmployee();
        if (employee != null) {
            employee.removeComputer(computer);
            employeeService.saveEmployee(employee);
        }
        computer.setEmployee(null);
        return computerRepository.save(computer);
    }
}
