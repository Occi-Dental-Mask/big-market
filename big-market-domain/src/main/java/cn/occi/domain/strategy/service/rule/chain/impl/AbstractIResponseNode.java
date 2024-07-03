package cn.occi.domain.strategy.service.rule.chain.impl;

import cn.occi.domain.strategy.service.rule.chain.IResponseNode;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
public abstract class AbstractIResponseNode implements IResponseNode {

    protected IResponseNode next;

    @Override
    public IResponseNode appendNext(IResponseNode next) {
        this.next = next;
        return next;
    }

    @Override
    public IResponseNode next() {
        return this.next;
    }
}
