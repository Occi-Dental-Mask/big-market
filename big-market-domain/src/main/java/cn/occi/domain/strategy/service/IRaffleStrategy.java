package cn.occi.domain.strategy.service;

import cn.occi.domain.strategy.model.entity.RaffleAwardEntity;
import cn.occi.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
public interface IRaffleStrategy {


    /**
     * 执行抽奖；用抽奖因子入参，执行抽奖计算，返回奖品信息
     * @param raffleFactorEntity
     * @return
     */
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
