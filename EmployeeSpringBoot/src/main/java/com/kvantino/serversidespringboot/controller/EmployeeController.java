package com.kvantino.serversidespringboot.controller;

import com.kvantino.serversidespringboot.model.Employee;
import com.kvantino.serversidespringboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {
    //private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    //get all Employees
    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> read() {
        final List<Employee> employees = employeeService.readAll();

        return employees != null && !employees.isEmpty()
                ? new ResponseEntity<>(employees, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //create Employee
    @PostMapping(value = "/employees")
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        employeeService.create(employee);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //get Employee by ID
    @GetMapping(value = "/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "id") long id) {
        Employee employee = employeeService.readById(id);

        return ResponseEntity.ok(employee);

//        return employee != null
//                ? new ResponseEntity<>(employee, HttpStatus.OK)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //update Employee data
    @PutMapping(value = "/employees/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") long id, @RequestBody Employee employee) {
        final boolean updated = employeeService.update(employee, id);

        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    //delete Employee data
    @DeleteMapping(value = "/employees/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok(employeeService.delete(id));
    }

//    @Autowired
//    public EmployeeController(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }

    //get all Employees
//    @GetMapping("/employees")
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
}
