package cn.occi.domain.strategy.model.entity;

import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleActionEntity <T extends RuleActionEntity.RaffleEntity>{

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    public static class RaffleEntity {
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RaffleBeforeEntity extends RaffleEntity{
        private Long strategyId;
        private String ruleWeightValueKey;
        private Integer awardId;
    }


}
