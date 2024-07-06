package cn.occi.domain.strategy.service.raffle;

import cn.occi.domain.strategy.model.vo.RuleTreeVO;
import cn.occi.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import cn.occi.domain.strategy.service.rule.tree.factory.engine.IDecisionEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Override
    protected TreeNodeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        String ruleModels = repository.queryStrategyRuleModel(strategyId, awardId);
        if (null == ruleModels) {
            return TreeNodeFactory.StrategyAwardVO.builder().awardId(awardId).build();
        }
        RuleTreeVO ruleTreeVO = repository.queryRuleTreeVOByTreeId(ruleModels);
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + ruleModels);
        }
        IDecisionEngine treeEngine = treeNodeFactory.openDecisionEngine(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        repository.updateStrategyAwardStock(strategyId, awardId);
    }

}
