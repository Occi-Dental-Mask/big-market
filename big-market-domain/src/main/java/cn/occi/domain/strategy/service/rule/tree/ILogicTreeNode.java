package cn.occi.domain.strategy.service.rule.tree;

import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
public interface ILogicTreeNode {

    TreeNodeFactory.TreeActionEntity executeNode(String userId, Long strategyId, Integer awardId);
}
