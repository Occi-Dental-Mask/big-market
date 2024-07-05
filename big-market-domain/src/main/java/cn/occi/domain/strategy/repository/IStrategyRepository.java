package cn.occi.domain.strategy.repository;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;
import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.model.vo.RuleTreeVO;

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
     * @param key
     * @param size
     * @param map
     */
    void storeStrategyAwardSearchRateTable(String key, int size, Map<Integer, Integer> map);

    /**
     * 获取奖品id
     * @param key
     * @param i
     * @return
     */
    Integer getRandomAwardId(String key, int i);

    /**
     * 获取概率区间
     * @param key
     * @return
     */
    Integer getRateRange(String key);

    /**
     * 查询策略规则
     * @param strategyId
     * @param ruleModel
     * @return
     */
    StrategyRuleEntity queryStrategyRule(Long strategyId, Integer awardId, String ruleModel);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    String queryStrategyRuleModel(Long strategyId, Integer awardId);

    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);
}
