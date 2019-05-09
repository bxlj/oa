package com.lj.oa.biz;

import com.lj.oa.entity.Employee;

public interface GlobalBiz {

    Employee login(String sn,String password);

    void changePassword(Employee employee);
}
