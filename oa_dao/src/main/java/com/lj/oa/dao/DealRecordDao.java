package com.lj.oa.dao;

import com.lj.oa.entity.DealRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 处理流程
 */
@Repository("dealRecordDao")
public interface DealRecordDao {

    void insert(DealRecord dealRecord);
    List<DealRecord> selectByClaimVoucher(int cvid);

}
