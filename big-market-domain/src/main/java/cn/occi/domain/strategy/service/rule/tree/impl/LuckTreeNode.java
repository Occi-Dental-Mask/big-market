package cn.occi.domain.strategy.service.rule.tree.impl;

import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
@Component("rule_luck_award")
public class LuckTreeNode implements ILogicTreeNode {
    @Override
    public TreeNodeFactory.TreeActionEntity executeNode(String userId, Long strategyId, Integer awardId) {
        return TreeNodeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardData(TreeNodeFactory.StrategyAwardData.builder()
                        .awardId(101)
                        .awardRuleValue("1,100")
                        .build())
                .build();
    }
}
