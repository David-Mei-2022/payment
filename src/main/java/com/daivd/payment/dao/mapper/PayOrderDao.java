package com.daivd.payment.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daivd.payment.dao.model.PayOrderPO;
import org.springframework.stereotype.Repository;

@Repository
public interface PayOrderDao extends BaseMapper<PayOrderPO> {
}
