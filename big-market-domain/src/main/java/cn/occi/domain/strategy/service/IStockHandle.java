package cn.occi.domain.strategy.service;

import cn.occi.domain.strategy.model.vo.StrategyAwardStockKeyVO;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/6
 */
public interface IStockHandle {

    /**
     * 获取奖品库存消耗队列
     *
     * @return 奖品库存Key信息
     * @throws InterruptedException 异常
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品库存消耗记录
     *
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);



}
