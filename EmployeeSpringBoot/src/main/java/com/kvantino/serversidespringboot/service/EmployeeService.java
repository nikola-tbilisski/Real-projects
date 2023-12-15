package com.kvantino.serversidespringboot.service;

import com.kvantino.serversidespringboot.model.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> readAll();
    void create(Employee employee);
    Employee readById(long id);
    boolean update(Employee employee, long id);
    Map<String, Boolean> delete(long id);
}
