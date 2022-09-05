package com.daivd.payment.service.impl;

import com.daivd.payment.convert.UnifiedPayConvert;
import com.daivd.payment.dao.mapper.PayOrderDao;
import com.daivd.payment.dao.model.PayOrderPO;
import com.daivd.payment.entity.BusinessCodeEnum;
import com.daivd.payment.entity.bo.UnifiedPayBO;
import com.daivd.payment.entity.dto.UnifiedPayDTO;
import com.daivd.payment.exception.ServiceException;
import com.daivd.payment.service.PayChannelService;
import com.daivd.payment.service.PayChannelServiceFactory;
import com.daivd.payment.service.PayService;
import com.daivd.payment.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PayServiceImpl implements PayService {
    /**
     * 定义分布锁Redis锁前缀
     */
    public final String redisLockPrefix = "pay-order&";

    /**
     * 引入Redis分布式锁依赖组件
     */
    @Autowired
    private RedisLockRegistry redisLockRegistry;

    /**
     * 支付订单持久层接口依赖
     */
    @Autowired
    PayOrderDao payOrderDao;

    /**
     * 支付渠道处理工厂类依赖
     */
    @Autowired
    PayChannelServiceFactory payChannelServiceFactory;

    @Override
    public UnifiedPayBO unifiedPay(UnifiedPayDTO unifiedPayDTO) {
        //返回数据对象
        UnifiedPayBO unifiedPayBO = null;
        //创建Redis分布式锁
        //支付防并发安全逻辑---通过“前缀 + 接入方业务订单号获取Redis分布式锁（同一笔订单，同一时刻只允许一个线程处理）”
        Lock lock = redisLockRegistry.obtain(redisLockPrefix + unifiedPayDTO.getOrderId());
        //持有锁，等待时间为1s
        boolean isLock = false;
        try {
            isLock = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isLock) {
            //数据库组别订单状态防重判断
            boolean isRepeatPayOrder = isSuccessPayOrder(unifiedPayDTO);
            if (isRepeatPayOrder) {
                throw new ServiceException(BusinessCodeEnum.BUSI_PAY_FAIL_1000.getCode(),
                        BusinessCodeEnum.BUSI_PAY_FAIL_1000.getDesc());
            }
            //支付订单入库
            String payId = this.payOrderSave(unifiedPayDTO);
            //获取具体的支付渠道服务类的实例
            PayChannelService payChannelService = payChannelServiceFactory.createPayChannelService(unifiedPayDTO.getChannel());
            //调用渠道支付方法设置支付平台订单流水号
            unifiedPayDTO.setOrderId(payId);
            unifiedPayBO = payChannelService.pay(unifiedPayDTO);
            //释放分布式锁
            lock.unlock();
        } else {
            //如果持有锁超时，则说明请求正在被处理，提示用户稍后重试
            throw new ServiceException(BusinessCodeEnum.BUSI_PAY_FAIL_1001.getCode(),
                    BusinessCodeEnum.BUSI_PAY_FAIL_1001.getDesc());
        }
        return unifiedPayBO;
    }


    /**
     * 数据库级别判断是否为成功支付订单私有方法
     *
     * @param unifiedPayDTO
     * @return
     */
    private boolean isSuccessPayOrder(UnifiedPayDTO unifiedPayDTO) {
        Map<String, Object> parm = new HashMap<>();
        parm.put("order_id", unifiedPayDTO.getOrderId());
        List<PayOrderPO> payOrderPOList = payOrderDao.selectByMap(parm);
        if (payOrderPOList != null && payOrderPOList.size() > 0) {
            //判断支付订单中是否存在成功状态的订单,存在则不处理新的支付请求
            List<PayOrderPO> successPayOrderList = payOrderPOList.stream()
                    .filter(o -> "2".equals(o.getStatus())).collect(Collectors.toList());
            if (successPayOrderList != null && successPayOrderList.size() > 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 支付订单入库方法
     *
     * @param unifiedPayDTO
     * @return
     */
    private String payOrderSave(UnifiedPayDTO unifiedPayDTO) {
        //用MapStruct工具进行实体对象类型转换
        PayOrderPO payOrderPO = UnifiedPayConvert.INSTANCE.convertPayOrderPO(unifiedPayDTO);
        //设置支付状态为"待支付"
        payOrderPO.setStatus("0");
        //生成支付平台流水号
        String payId = createPayId();
        payOrderPO.setPayId(payId);
        //订单创建时间
        payOrderPO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //订单更新时间
        payOrderPO.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        //订单入库操作
        payOrderDao.insert(payOrderPO);
        return payOrderPO.getPayId();
    }

    /**
     * 生成支付平台订单号
     *
     * @return
     */
    private String createPayId() {
        //获取10000～99999随机数
        Integer random = new Random().nextInt(99999) % (99999 - 10000 + 1) + 10000;
        //时间戳+随机数
        String payId = DateUtils.getStringByFormat(new Date(), DateUtils.sf3) + String.valueOf(random);
        return payId;
    }
}
