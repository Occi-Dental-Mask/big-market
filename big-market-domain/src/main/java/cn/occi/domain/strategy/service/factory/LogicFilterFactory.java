package cn.occi.domain.strategy.service.factory;

import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.service.annotation.LogicStrategy;
import cn.occi.domain.strategy.service.rule.IRuleFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Service
public class LogicFilterFactory {

    public  Map<String, IRuleFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public LogicFilterFactory(List<IRuleFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, IRuleFilter<T>> openLogicFilter() {
        return (Map<String, IRuleFilter<T>>) (Map<?, ?>) logicFilterMap;
    }


    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WEIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),
        RULE_LOCK("rule_lock","【抽奖中规则】锁定规则过滤，命中锁定规则则直接返回")
        ;

        private final String code;
        private final String info;

    }
}
