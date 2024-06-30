package cn.occi.domain.strategy.repository;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/29
 */
public interface IStrategyRepository {
    /**
     * 根据策略id查询奖品列表
     * @param strategyId
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    /**
     * 存储策略奖品搜索概率表
     * @param strategyId
     * @param size
     * @param map
     */
    void storeStrategyAwardSearchRateTable(Long strategyId, int size, Map<Integer, Integer> map);

    /**
     * 获取奖品id
     * @param strategyId
     * @param i
     * @return
     */
    Integer getRandomAwardId(Long strategyId, int i);

    /**
     * 获取概率区间
     * @param strategyId
     * @return
     */
    int getRateRange(Long strategyId);
}
