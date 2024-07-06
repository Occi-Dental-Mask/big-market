package cn.occi.domain.strategy.service.orm;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/30
 */
public interface IStrategyDispatch {


    /**
     * 根据策略id随机获得奖品id
     */
    public Integer getRandomAwardId(Long strategyId);

    /**
     * 根据策略id和权重策略随机获得奖品id
     */
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /***
     * 减少库存
     * @return
     */
    Boolean deductAwardStock(Long strategyId, Integer awardId);
}
