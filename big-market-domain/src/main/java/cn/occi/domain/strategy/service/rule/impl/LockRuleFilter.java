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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO.ALLOW;
import static cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO.TAKE_OVER;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/2
 */
@Slf4j
@Component
@LogicStrategy(logicMode = LogicFilterFactory.LogicModel.RULE_LOCK)
public class LockRuleFilter implements IRuleFilter {
    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public RuleActionEntity filterRule(RuleMatterEntity ruleMatterEntity) {
        log.info("中置规则过滤-锁定 userId:{} strategyId:{} awardId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        // 查询对应奖品id的锁定规则
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        if (strategyRuleEntity == null) {
            // 没有这个规则，放行
            return RuleActionEntity.builder().code(ALLOW.getCode()).info(ALLOW.getInfo()).build();
        }
        int userCount = 0;
        Long requestCount = Long.valueOf(strategyRuleEntity.getRuleValue());
        // 判断用户抽奖次数是否大于等于要求的抽奖次数
        if (userCount > requestCount) {
            return RuleActionEntity.builder().code(ALLOW.getCode()).info(ALLOW.getInfo()).build();
        }
        // 抽奖次数没有达到要求，接管，后续需要锁定这个奖品
        return RuleActionEntity.builder().code(TAKE_OVER.getCode()).info(TAKE_OVER.getInfo()).ruleModel(ruleMatterEntity.getRuleModel()).
                data(RuleActionEntity.RaffleDuringEntity.builder().countRange(requestCount).
                        strategyId(ruleMatterEntity.getStrategyId()).awardId(ruleMatterEntity.getAwardId()).build())
                .build();
    }
}
