package cn.occi.domain.strategy.service.rule.chain;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
public interface IResponseNode extends IResponseNodeArm{

    Integer executeNode(String userId, Long strategyId);
}
