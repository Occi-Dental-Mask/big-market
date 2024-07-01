package cn.occi.domain.strategy.service.annotation;

import cn.occi.domain.strategy.service.factory.LogicFilterFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {

        LogicFilterFactory.LogicModel logicMode();
}
