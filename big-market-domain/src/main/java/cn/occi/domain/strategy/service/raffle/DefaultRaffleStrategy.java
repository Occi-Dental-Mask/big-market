package cn.occi.domain.strategy.service.raffle;

import cn.occi.domain.strategy.model.entity.RaffleFactorEntity;
import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.RuleMatterEntity;
import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.model.vo.RuleTreeVO;
import cn.occi.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.factory.LogicFilterFactory;
import cn.occi.domain.strategy.service.rule.IRuleFilter;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import cn.occi.domain.strategy.service.rule.tree.factory.engine.IDecisionEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO.TAKE_OVER;
import static cn.occi.domain.strategy.service.factory.LogicFilterFactory.LogicModel.RULE_LOCK;

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
}
