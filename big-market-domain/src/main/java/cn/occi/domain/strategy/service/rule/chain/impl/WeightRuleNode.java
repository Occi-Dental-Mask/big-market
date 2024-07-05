package cn.occi.domain.strategy.service.rule.chain.impl;

import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.factory.LogicFilterFactory;
import cn.occi.domain.strategy.service.orm.IStrategyDraw;
import cn.occi.domain.strategy.service.rule.chain.IResponseNode;
import cn.occi.domain.strategy.service.rule.chain.factory.ChainNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
@Component("rule_weight")
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class WeightRuleNode extends AbstractIResponseNode {
    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private IStrategyDraw strategyDraw;
    @Override
    public ChainNodeFactory.StrategyAwardVO executeNode(String userId, Long strategyId) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, "rule_weight");

        // 查询StrategyRule
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, null, "rule_weight");

        // 获取到ruleValue
        Map<Long, String> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();

        // 获取到用户的积分
        Long userScore = 4500L; // 暂时设置为固定值 todo 之后修改

        // 按照ruleWeightValues的key值进行排序，然后找到第一个大于等于userScore的key值
        List<Long> sortedKeys = new ArrayList<>(ruleWeightValues.keySet());
        Collections.sort(sortedKeys);
        // 流式操作找出第一个小于userScore的key值
        Long key = sortedKeys.stream().filter(k -> k <= userScore).findFirst().orElse(null);
        if (key != null) {
            // 接管
            Integer awardId = strategyDraw.getRandomAwardId(strategyId, String.valueOf(key));
            return ChainNodeFactory.StrategyAwardVO.builder().awardId(awardId).
                    logicModel(ChainNodeFactory.LogicModel.RULE_WEIGHT.getCode()).build();
        }
        //没有大于userScore的值，无需进行操作，放行
        return next().executeNode(userId, strategyId);
    }
}
