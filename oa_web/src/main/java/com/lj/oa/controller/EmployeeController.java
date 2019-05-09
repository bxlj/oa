package com.lj.oa.controller;

import com.lj.oa.biz.DepartmentBiz;
import com.lj.oa.biz.EmployeeBiz;
import com.lj.oa.entity.Employee;
import com.lj.oa.global.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller("employeeController")
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private DepartmentBiz departmentBiz;
    @Autowired
    private EmployeeBiz employeeBiz;

    /**
     * 员工列表
     * @param map
     * @return
     */
    @RequestMapping("/list")
    public String list(Map<String,Object> map){
        map.put("list",employeeBiz.getAll());
        return "employee_list";
    }

    @RequestMapping("/to_add")
    public String toAdd(Map<String,Object> map){
        map.put("employee",new Employee());
        map.put("dlist",departmentBiz.getAll());
        map.put("plist", Content.getPosts());
        return "employee_add";
    }

    /**
     * 添加员工信息
     * @param employee
     * @return
     */
    @RequestMapping("/add")
    public String add(Employee employee){
        employeeBiz.add(employee);
        return "redirect:list";
    }

    @RequestMapping(value = "/to_update",params = "sn")
    public String toUpdate(String sn,Map<String,Object> map){
        map.put("employee",employeeBiz.get(sn));
        map.put("dlist",departmentBiz.getAll());
        map.put("plist", Content.getPosts());
        return "employee_update";
    }

    /**
     * 修改员工信息
     * @param employee
     * @return
     */
    @RequestMapping("/update")
    public String update(Employee employee){
        employeeBiz.edit(employee);
        return "redirect:list";
    }

    /**
     * 删除员工信息
     * @param sn
     * @return
     */
    @RequestMapping("/remove")
    public String remove(String sn){
        employeeBiz.remove(sn);
        return "redirect:list";
    }

}
