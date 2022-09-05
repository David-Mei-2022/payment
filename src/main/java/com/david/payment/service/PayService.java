package com.david.payment.service;

import com.david.payment.entity.bo.UnifiedPayBO;
import com.david.payment.entity.dto.UnifiedPayDTO;

public interface PayService {
    UnifiedPayBO unifiedPay(UnifiedPayDTO unifiedPayDTO);
}
