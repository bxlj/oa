package com.lj.oa.controller;

import com.lj.oa.biz.ClaimVoucherBiz;
import com.lj.oa.dto.ClaimVoucherInfo;
import com.lj.oa.entity.DealRecord;
import com.lj.oa.entity.Employee;
import com.lj.oa.global.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller("claimVoucherController")
@RequestMapping("/claim_voucher")
public class ClaimVoucherController {

    @Autowired
    private ClaimVoucherBiz claimVoucherBiz;

    @RequestMapping("/to_add")
    public String toAdd(Map<String,Object> map){
        map.put("items",Content.getItems());
        //使用mvc的from表单
        map.put("info",new ClaimVoucherInfo());
        return "claim_voucher_add";
    }

    /**
     * 添加报销单
     * @param session
     * @param info
     * @return
     */
    @RequestMapping("/add")
    public String add(HttpSession session,ClaimVoucherInfo info){
        Employee employee = (Employee) session.getAttribute("employee");
        info.getClaimVoucher().setCreateSn(employee.getSn());
        claimVoucherBiz.save(info.getClaimVoucher(),info.getItems());
        return "redirect:deal";
    }

    /**
     * 报销单详情
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("/detail")
    public String detail(int id,Map<String,Object> map){
        map.put("claimVoucher",claimVoucherBiz.get(id));
        map.put("items",claimVoucherBiz.getItems(id));
        map.put("records",claimVoucherBiz.getRecords(id));
        return "claim_voucher_detail";
    }

    /**
     * 个人报销单
     * @param session
     * @param map
     * @return
     */
    @RequestMapping("/self")
    public String self(HttpSession session,Map<String,Object> map){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("list",claimVoucherBiz.selectByCreateSn(employee.getSn()));
        return "claim_voucher_self";
    }


    /**
     * 待处理报销单
     * @param session
     * @param map
     * @return
     */
    @RequestMapping("/deal")
    public String deal(HttpSession session,Map<String,Object> map){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("list",claimVoucherBiz.selectByNextDealSn(employee.getSn()));
        return "claim_voucher_deal";
    }


    @RequestMapping("/to_update")
    public String toUpdate(int id,Map<String,Object> map){
        map.put("items",Content.getItems());
        ClaimVoucherInfo info = new ClaimVoucherInfo();
        info.setClaimVoucher(claimVoucherBiz.get(id));
        info.setItems(claimVoucherBiz.getItems(id));
        map.put("info",info);
        return "claim_voucher_update";
    }

    /**
     * 修改报销单
     * @param session
     * @param info
     * @return
     */
    @RequestMapping("/update")
    public String update(HttpSession session,ClaimVoucherInfo info){
        Employee employee = (Employee) session.getAttribute("employee");
        info.getClaimVoucher().setCreateSn(employee.getSn());
        claimVoucherBiz.update(info.getClaimVoucher(),info.getItems());
        return "redirect:deal";
    }

    /**
     * 提交报销单
     * @param id 报销单编号
     * @return
     */
    @RequestMapping("/submit")
    public String submit(int id){
        System.out.println("ID"+id);
        claimVoucherBiz.submit(id);
        return "redirect:deal";
    }

    /**
     *
     * @param id 报销单编号
     * @param map
     * @return
     */
    @RequestMapping("/to_check")
    public String toCheck(int id,Map<String,Object> map){
        map.put("claimVoucher",claimVoucherBiz.get(id));
        map.put("item",claimVoucherBiz.getItems(id));
        map.put("records",claimVoucherBiz.getRecords(id));
        DealRecord dealRecord = new DealRecord();
        dealRecord.setClaimVoucherId(id);
        map.put("record",dealRecord);
        return "claim_voucher_check";
    }


    /**
     * 处理报销单
     * @param session
     * @param dealRecord
     * @return
     */
    @RequestMapping("/check")
    public String check(HttpSession session,DealRecord dealRecord){
        Employee employee = (Employee) session.getAttribute("employee");
        dealRecord.setDealSn(employee.getSn());
        claimVoucherBiz.deal(dealRecord);
        return "redirect:deal";
    }



}
