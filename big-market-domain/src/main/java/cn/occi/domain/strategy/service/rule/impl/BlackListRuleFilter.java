package cn.occi.domain.strategy.service.rule.impl;

import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.RuleMatterEntity;
import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.annotation.LogicStrategy;
import cn.occi.domain.strategy.service.factory.LogicFilterFactory;
import cn.occi.domain.strategy.service.rule.IRuleFilter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Rule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Component
@LogicStrategy(logicMode = LogicFilterFactory.LogicModel.RULE_BLACKLIST)
@Slf4j
public class BlackListRuleFilter implements IRuleFilter {
    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public RuleActionEntity filterRule(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        // 查询黑名单
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        if (strategyRuleEntity == null) {
            // 没有这个规则，放行
            return RuleActionEntity.builder().code(RuleLogicCheckTypeVO.ALLOW.getCode()).info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 判断用户是否在黑名单中
        Map<Integer, List<String>> blackListUserValues = strategyRuleEntity.getBlackListUserValues();
        for (Map.Entry<Integer, List<String>> entry: blackListUserValues.entrySet()) {
            if (entry.getValue().contains(userId)) {
                return RuleActionEntity.builder().code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo()).ruleModel(ruleMatterEntity.getRuleModel()).
                        data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .awardId(entry.getKey()).ruleWeightValueKey(strategyRuleEntity.getRuleValue()).strategyId(ruleMatterEntity.getStrategyId()).build())
                        .build();
            }
        }
        // 用户不在黑名单中，放行
        return RuleActionEntity.builder().code(RuleLogicCheckTypeVO.ALLOW.getCode()).info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();

    }
}
