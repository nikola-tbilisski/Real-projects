package com.kvantino.serversidespringboot.service;

import com.kvantino.serversidespringboot.exception.ResourceNotFoundException;
import com.kvantino.serversidespringboot.model.Employee;
import com.kvantino.serversidespringboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    final private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> readAll() {
        return employeeRepository.findAll();
    }

    @Override
    public void create(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public Employee readById(long id) {
        //return employeeRepository.getReferenceById(id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sorry no data for Employee with id: " + id));
    }

    @Override
    public boolean update(Employee employee, long id) {
        if (employeeRepository.existsById(id)) {
            employee.setId(id);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Boolean> delete(long id) {
        employeeRepository.delete(employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id: " + id + " doesn't exist")));

        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted", Boolean.TRUE);

        return response;
    }


}
