package cn.occi.domain.strategy.service.rule.tree.factory.engine;

import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
public interface IDecisionEngine {

    TreeNodeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);

}
