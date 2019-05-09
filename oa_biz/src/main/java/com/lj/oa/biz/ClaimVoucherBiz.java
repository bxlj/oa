package com.lj.oa.biz;

import com.lj.oa.entity.ClaimVoucher;
import com.lj.oa.entity.ClaimVoucherItem;
import com.lj.oa.entity.DealRecord;

import java.util.List;

public interface ClaimVoucherBiz {

    //保存报销单
    void save(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items);

    ClaimVoucher get(int id);
    List<ClaimVoucherItem> getItems(int cvid);
    List<DealRecord> getRecords(int cvid);

    List<ClaimVoucher> selectByCreateSn(String sn);
    List<ClaimVoucher> selectByNextDealSn(String ndsn);

    void update(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items);

    void submit(int id);

    void deal(DealRecord dealRecord);
}
