package com.lj.oa.biz;

import com.lj.oa.entity.Employee;

import java.util.List;

public interface EmployeeBiz {

    void add(Employee employee);
    void remove(String sn);
    void edit(Employee employee);
    Employee get(String sn);
    List<Employee> getAll();
}
