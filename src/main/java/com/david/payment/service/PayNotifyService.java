package com.david.payment.service;

import com.david.payment.entity.dto.AliPayReceiveDTO;

public interface PayNotifyService {

    /**
     * 支付宝异步支付结果通知
     *
     * @param aliPayReceiveDTO
     * @return
     */
    String aliPayReceive(AliPayReceiveDTO aliPayReceiveDTO);

}
