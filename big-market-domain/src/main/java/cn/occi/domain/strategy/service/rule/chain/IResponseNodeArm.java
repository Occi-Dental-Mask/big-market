package cn.occi.domain.strategy.service.rule.chain;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
public interface IResponseNodeArm {
    /**
     * 装配下一个节点
     */
    IResponseNode appendNext(IResponseNode next);

    /**
     * 获取下一个节点
     */
    IResponseNode next();

}
