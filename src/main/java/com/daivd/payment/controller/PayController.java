package com.daivd.payment.controller;

import com.daivd.payment.entity.ResponseResult;
import com.daivd.payment.entity.bo.UnifiedPayBO;
import com.daivd.payment.entity.dto.UnifiedPayDTO;
import com.daivd.payment.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    PayService payServiceImpl;

    @PatchMapping("/unifiedPay")
    public ResponseResult<UnifiedPayBO> unifiedPay(@RequestBody @Validated UnifiedPayDTO unifiedPayDTO) {
        return ResponseResult.OK(payServiceImpl.unifiedPay(unifiedPayDTO));
    }
}
