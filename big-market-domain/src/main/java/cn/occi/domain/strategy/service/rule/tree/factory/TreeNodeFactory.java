package cn.occi.domain.strategy.service.rule.tree.factory;

import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.model.vo.RuleTreeVO;
import cn.occi.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.occi.domain.strategy.service.rule.tree.factory.engine.DecisionEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
@Service
public class TreeNodeFactory {
    Map<String, ILogicTreeNode> treeNodeGroup;

    public TreeNodeFactory(Map<String, ILogicTreeNode> treeNodeGroup) {
        this.treeNodeGroup = treeNodeGroup;
    }

    public DecisionEngine openDecisionEngine(RuleTreeVO ruleTreeVO) {
        return new DecisionEngine(this.treeNodeGroup, ruleTreeVO);
    }

    /**
     * 决策树返回放行还是拦截
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {
        private RuleLogicCheckTypeVO ruleLogicCheckType;
        private StrategyAwardVO strategyAwardVO;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /** 抽奖奖品规则 */
        private String awardRuleValue;
    }

}
