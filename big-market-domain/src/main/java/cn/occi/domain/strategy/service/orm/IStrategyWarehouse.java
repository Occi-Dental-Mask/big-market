package cn.occi.domain.strategy.service.orm;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/29
 */
public interface IStrategyWarehouse {

    /**
     * 装配策略配置
     */
    public boolean assembleStrategyConfig(Long strategyId);

    /**
     * 根据策略id随机获得奖品id
     */
    public Integer getRandomAwardId(Long strategyId);

}
