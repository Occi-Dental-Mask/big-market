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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Component
@Slf4j
@LogicStrategy(logicMode = LogicFilterFactory.LogicModel.RULE_WEIGHT)
public class WeightRuleFilter implements IRuleFilter {


    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public RuleActionEntity filterRule(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        // 查询StrategyRule
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

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
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(ruleMatterEntity.getStrategyId())
                            .ruleWeightValueKey(String.valueOf(key)) //todo key应该是5000，不带后面那些玩意
                            .build())
                    .ruleModel(LogicFilterFactory.LogicModel.RULE_WEIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }
        //没有大于userScore的值，无需进行操作，放行
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder().code(RuleLogicCheckTypeVO.ALLOW.getCode()).info(RuleLogicCheckTypeVO.ALLOW.getInfo()).build();

    }
}
