package cn.occi.domain.strategy.service.rule.tree.factory.engine;

import cn.occi.domain.strategy.model.vo.*;
import cn.occi.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
@Slf4j
public class DecisionEngine implements IDecisionEngine{

    private final Map<String, ILogicTreeNode> treeNodeGroup;
    private final RuleTreeVO ruleTreeVO;
    public DecisionEngine(Map<String, ILogicTreeNode>  treeNodeGroup, RuleTreeVO ruleTreeVO) {
        this. treeNodeGroup=  treeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }
    public TreeNodeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId) {
        String rootNodeName = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();
        String nextNodeName = rootNodeName;
        RuleTreeNodeVO rootNode = treeNodeMap.get(rootNodeName);
        RuleTreeNodeVO pointNode = rootNode;
        TreeNodeFactory.StrategyAwardData strategyAwardData = null;
        while (pointNode != null) {
            ILogicTreeNode logicTreeNode = treeNodeGroup.get(pointNode.getRuleKey());
            TreeNodeFactory.TreeActionEntity treeActionEntity = logicTreeNode.executeNode(userId, strategyId, awardId);
            List<RuleTreeNodeLineVO> treeNodeLineVOList = pointNode.getTreeNodeLineVOList();
            strategyAwardData = treeActionEntity.getStrategyAwardData();
            log.info("决策树引擎【{}】treeId:{} node:{} info:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNodeName, treeActionEntity.getRuleLogicCheckType().getInfo());
            if (ruleTreeVO == null || treeNodeLineVOList == null || treeNodeLineVOList.isEmpty() ){
                break;
            }
            for (RuleTreeNodeLineVO ruleTreeNodeLineVO : treeNodeLineVOList) {
                RuleLimitTypeVO ruleLimitType = ruleTreeNodeLineVO.getRuleLimitType();
                RuleLogicCheckTypeVO ruleLimitValue = ruleTreeNodeLineVO.getRuleLimitValue();
                if (decideWhetherNextNode(ruleLimitType, ruleLimitValue, treeActionEntity.getRuleLogicCheckType().getCode())) {
                    nextNodeName = ruleTreeNodeLineVO.getRuleNodeTo();
                    pointNode = treeNodeMap.get(nextNodeName);
                    break;
                }
            }
        }
        return strategyAwardData;


    }

    private boolean decideWhetherNextNode(RuleLimitTypeVO ruleLimitType, RuleLogicCheckTypeVO ruleLimitValue, String checkTypeCode) {
        switch (ruleLimitType) {
            case EQUAL:
                return ruleLimitValue.getCode().equals(checkTypeCode);
            case GE:
            case GT:
            case LE:
            case LT:
            default:
                return false;
        }
    }
}
