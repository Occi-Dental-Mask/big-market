package cn.occi.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
@Getter
@AllArgsConstructor
public enum RuleLimitTypeVO {
    EQUAL(1, "等于"),
    GT(2, "大于"),
    LT(3, "小于"),
    GE(4, "大于&等于"),
    LE(5, "小于&等于"),
    ENUM(6, "枚举"),
            ;

    private final Integer code;
    private final String info;
}
