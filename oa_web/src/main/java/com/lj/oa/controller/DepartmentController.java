package com.lj.oa.controller;

import com.lj.oa.biz.DepartmentBiz;
import com.lj.oa.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller("departmentController")
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentBiz departmentBiz;

    /**
     * 部门列表
     * @param map
     * @return
     */
    @RequestMapping("/list")
    public String list(Map<String,Object> map){
        map.put("list",departmentBiz.getAll());
        return "department_list";
    }

    @RequestMapping("/to_add")
    public String toAdd(Map<String,Object> map){
        map.put("department",new Department());
        return "department_add";
    }

    /**
     * 添加部门信息
     * @param department
     * @return
     */
    @RequestMapping("/add")
    public String add(Department department){
        departmentBiz.add(department);
        return "redirect:list";
    }

    @RequestMapping(value = "/to_update",params = "sn")
    public String toUpdate(String sn,Map<String,Object> map){
        map.put("department",departmentBiz.get(sn));
        return "department_update";
    }

    /**
     * 修改部门信息
     * @param department
     * @return
     */
    @RequestMapping("/update")
    public String update(Department department){
        departmentBiz.edit(department);
        return "redirect:list";
    }

    /**
     * 删除部门信息
     * @param sn
     * @return
     */
    @RequestMapping("/remove")
    public String remove(String sn){
        departmentBiz.remove(sn);
        return "redirect:list";
    }

}
