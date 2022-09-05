package com.daivd.payment.service;

import com.daivd.payment.entity.bo.UnifiedPayBO;
import com.daivd.payment.entity.dto.UnifiedPayDTO;

/**
 * @author jiangqiao
 */
public interface PayChannelService {

    /**
     * 渠道支付接入入口方法
     *
     * @param unifiedPayDTO
     * @return
     */
    UnifiedPayBO pay(UnifiedPayDTO unifiedPayDTO);
}
