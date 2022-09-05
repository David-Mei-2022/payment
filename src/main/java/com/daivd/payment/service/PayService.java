package com.daivd.payment.service;

import com.daivd.payment.entity.bo.UnifiedPayBO;
import com.daivd.payment.entity.dto.UnifiedPayDTO;

public interface PayService {
    UnifiedPayBO unifiedPay(UnifiedPayDTO unifiedPayDTO);
}
