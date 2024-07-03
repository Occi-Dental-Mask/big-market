package cn.occi.domain.strategy.service.rule.chain.impl;

import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
@Component("rule_blacklist")
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlockListRuleNode extends AbstractIResponseNode {
    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public Integer executeNode(String userId, Long strategyId) {
        log.info("规则过滤-黑名单 userId:{} strategyId:{} ruleModel:{rule_blacklist}", userId, strategyId);

        // 查询黑名单
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, null, "rule_blacklist");
        if (strategyRuleEntity == null) {
            // 没有任何策略规则，放行
            return null;
        }

        // 判断用户是否在黑名单中
        Map<Integer, List<String>> blackListUserValues = strategyRuleEntity.getBlackListUserValues();
        for (Map.Entry<Integer, List<String>> entry : blackListUserValues.entrySet()) {
            if (entry.getValue().contains(userId)) {
                return entry.getKey();
            }
        }
        // 用户不在黑名单中，放行
        return next().executeNode(userId, strategyId);

    }
}
