package com.david.payment.controller;

import com.david.payment.entity.ResponseResult;
import com.david.payment.entity.bo.UnifiedPayBO;
import com.david.payment.entity.dto.UnifiedPayDTO;
import com.david.payment.service.PayService;
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
