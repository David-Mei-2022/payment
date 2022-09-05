package com.david.payment.convert;

import com.david.payment.dao.model.PayNotifyPO;
import com.david.payment.dao.model.PayOrderPO;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author jiangqiao
 */
@org.mapstruct.Mapper
public interface PayNotifyConvert {

    PayNotifyConvert INSTANCE = Mappers.getMapper(PayNotifyConvert.class);

    /**
     * 支付通知报文日志信息转换方法
     *
     * @param payOrderPO
     * @return
     */
    @Mappings({})
    PayNotifyPO convertPayNotifyPO(PayOrderPO payOrderPO);
}
