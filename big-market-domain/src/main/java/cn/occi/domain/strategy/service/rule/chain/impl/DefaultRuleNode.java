package cn.occi.domain.strategy.service.rule.chain.impl;

import cn.occi.domain.strategy.service.orm.IStrategyDispatch;
import cn.occi.domain.strategy.service.rule.chain.IResponseNode;
import cn.occi.domain.strategy.service.rule.chain.factory.ChainNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
@Component("rule_default")
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DefaultRuleNode extends AbstractIResponseNode {
    @Resource
    private IStrategyDispatch strategyDraw;
    @Override
    public ChainNodeFactory.StrategyAwardVO executeNode(String userId, Long strategyId) {
        // 4. 默认抽奖流程
        Integer awardId = strategyDraw.getRandomAwardId(strategyId);
        return ChainNodeFactory.StrategyAwardVO.builder().awardId(awardId).
                logicModel(ChainNodeFactory.LogicModel.RULE_DEFAULT.getCode()).build();
    }
}
