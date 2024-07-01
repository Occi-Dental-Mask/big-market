package cn.occi.domain.strategy.service.rule;

import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
public interface IRuleFilter<T extends RuleActionEntity.RaffleEntity> {
    RuleActionEntity<T> filterRule(RuleMatterEntity ruleMatterEntity);
}
