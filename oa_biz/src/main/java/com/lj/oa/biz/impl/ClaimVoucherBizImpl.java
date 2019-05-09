package com.lj.oa.biz.impl;

import com.lj.oa.biz.ClaimVoucherBiz;
import com.lj.oa.dao.ClaimVoucherDao;
import com.lj.oa.dao.ClaimVoucherItemDao;
import com.lj.oa.dao.DealRecordDao;
import com.lj.oa.dao.EmployeeDao;
import com.lj.oa.entity.ClaimVoucher;
import com.lj.oa.entity.ClaimVoucherItem;
import com.lj.oa.entity.DealRecord;
import com.lj.oa.entity.Employee;
import com.lj.oa.global.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service("claimVoucher")
public class ClaimVoucherBizImpl implements ClaimVoucherBiz {

    @Qualifier("claimVoucherDao")
    @Autowired
    private ClaimVoucherDao claimVoucherDao;
    @Qualifier("claimVoucherItemDao")
    @Autowired
    private ClaimVoucherItemDao claimVoucherItemDao;
    @Qualifier("dealRecordDao")
    @Autowired
    private DealRecordDao dealRecordDao;
    @Qualifier("employeeDao")
    @Autowired
    private EmployeeDao employeeDao;


    /**
     * 保存报销单
     * @param claimVoucher
     * @param items
     */
    public void save(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setCreateTime(new Date());
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Content.CLAIMVOUCHER_CREATED);
        claimVoucherDao.insert(claimVoucher);

        for (ClaimVoucherItem item:items) {
            item.setClaimVoucherId(claimVoucher.getId());
            claimVoucherItemDao.insert(item);
        }
    }

    /**
     * 获取报销单
     * @param id
     * @return
     */
    public ClaimVoucher get(int id) {
        return claimVoucherDao.select(id);
    }

    public List<ClaimVoucherItem> getItems(int cvid) {
        return claimVoucherItemDao.selectByClaimVoucher(cvid);
    }

    public List<DealRecord> getRecords(int cvid) {
        return dealRecordDao.selectByClaimVoucher(cvid);
    }


    /**
     * 个人报销单
     * @param sn
     * @return
     */
    public List<ClaimVoucher> selectByCreateSn(String sn) {
        return claimVoucherDao.selectByCreateSn(sn);
    }


    /**
     * 待处理报销单
     * @param ndsn 待处理人编号
     * @return
     */
    public List<ClaimVoucher> selectByNextDealSn(String ndsn) {
        return claimVoucherDao.selectByNextDealSn(ndsn);
    }

    /**
     * 修改报销单
     * @param claimVoucher 报销单
     * @param items
     */
    public void update(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Content.CLAIMVOUCHER_CREATED);
        claimVoucherDao.update(claimVoucher);

        System.out.println(claimVoucher.getId());
        List<ClaimVoucherItem> olds = claimVoucherItemDao.selectByClaimVoucher(claimVoucher.getId());
        for(ClaimVoucherItem old:olds){
            boolean isHave=false;
            for(ClaimVoucherItem item:items){
                if(item.getId()==old.getId()){
                    isHave=true;
                    break;
                }
            }
            if(!isHave){
                claimVoucherItemDao.delete(old.getId());
            }
        }
        for(ClaimVoucherItem item:items){
            item.setClaimVoucherId(claimVoucher.getId());
            if(item.getId() != null && item.getId()>0){
                claimVoucherItemDao.update(item);
            }else{
                claimVoucherItemDao.insert(item);
            }
        }
    }

    /**
     * 提交报销单
     * @param id 报销单编号
     */
    public void submit(int id) {
        ClaimVoucher claimVoucher = claimVoucherDao.select(id);
        Employee employee = employeeDao.select(claimVoucher.getCreateSn());

        claimVoucher.setStatus(Content.CLAIMVOUCHER_SUBMIT);
        claimVoucher.setNextDealSn((employeeDao.selectByDepartmentAndPost(employee.getDepartmentSn(),Content.POST_FM)).get(0).getSn());

        claimVoucherDao.update(claimVoucher);

        DealRecord dealRecord = new DealRecord();
        dealRecord.setDealWay(Content.DEAL_SUBMIT);
        dealRecord.setClaimVoucherId(id);
        dealRecord.setDealTime(new Date());
        dealRecord.setDealResult(Content.CLAIMVOUCHER_SUBMIT);
        dealRecord.setComment("无");
        dealRecord.setDealSn(employee.getSn());

        dealRecordDao.insert(dealRecord);


    }

    /**
     * 处理报销单
     * @param dealRecord
     */
    public void deal(DealRecord dealRecord) {
        ClaimVoucher claimVoucher = claimVoucherDao.select(dealRecord.getClaimVoucherId());
        Employee employee = employeeDao.select(dealRecord.getDealSn());

        dealRecord.setDealTime(new Date());

        if (dealRecord.getDealWay().equals(Content.DEAL_PASS)){
            //以通过
            if (claimVoucher.getTotalAmount() <= Content.LIMIT_CHECK || employee.getPost().equals(Content.POST_GM)){
                claimVoucher.setStatus(Content.CLAIMVOUCHER_APPROVED);
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null,Content.POST_CASHIER).get(0).getSn());
                dealRecord.setDealResult(Content.CLAIMVOUCHER_APPROVED);
            }else {
                claimVoucher.setStatus(Content.CLAIMVOUCHER_RECHECK);
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null,Content.POST_GM).get(0).getSn());
                dealRecord.setDealResult(Content.CLAIMVOUCHER_RECHECK);
            }
        }else if (dealRecord.getDealWay().equals(Content.DEAL_BACK)){
            //以打回
            claimVoucher.setStatus(Content.CLAIMVOUCHER_BACK);
            claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
            dealRecord.setDealResult(Content.DEAL_BACK);

        }else if (dealRecord.getDealWay().equals(Content.DEAL_REJECT)){
            //以拒绝
            claimVoucher.setStatus(Content.CLAIMVOUCHER_TERMINATED);
            claimVoucher.setNextDealSn(null);
            dealRecord.setDealResult(Content.CLAIMVOUCHER_TERMINATED);

        }else if (dealRecord.getDealWay().equals(Content.DEAL_PAID)){
            //以打款
            claimVoucher.setStatus(Content.CLAIMVOUCHER_PAID);
            claimVoucher.setNextDealSn(null);
            dealRecord.setDealResult(Content.CLAIMVOUCHER_PAID);

        }
        claimVoucherDao.update(claimVoucher);
        dealRecordDao.insert(dealRecord);
    }


}
