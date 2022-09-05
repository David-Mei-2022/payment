package com.daivd.payment.service;

import com.daivd.payment.entity.dto.AliPayReceiveDTO;
public interface PayNotifyService {

    /**
     * 支付宝异步支付结果通知
     *
     * @param aliPayReceiveDTO
     * @return
     */
    String aliPayReceive(AliPayReceiveDTO aliPayReceiveDTO);

}
