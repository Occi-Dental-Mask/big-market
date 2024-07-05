package cn.occi.domain.strategy.service.rule.chain;

import cn.occi.domain.strategy.service.rule.chain.factory.ChainNodeFactory;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
public interface IResponseNode extends IResponseNodeArm{

    ChainNodeFactory.StrategyAwardVO executeNode(String userId, Long strategyId);
}
