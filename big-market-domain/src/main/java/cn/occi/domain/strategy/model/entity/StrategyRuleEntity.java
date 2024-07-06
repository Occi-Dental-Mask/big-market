package cn.occi.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyRuleEntity {
    private Long strategyId;
    private Integer awardId;
    private Integer ruleType;
    private String ruleModel;
    private String ruleValue;
    private String ruleDesc;

    public Map<String, List<Integer>> getRuleWeightLists() {
        // 根据ruleValue解析出ruleWeight和ruleValues
//        6000:102,103,104,105,106,107,108,109 5000:101,102,103,104,105,106,107,108,109
        Map<String, List<Integer>> res = new HashMap<>();
        String[] ruleValueGroups = ruleValue.split(" ");
        for (String ruleValueGroup : ruleValueGroups) {
            String[] parts = ruleValueGroup.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueGroup);
            }
            String ruleWeight = parts[0];
            String[] valueStrings = parts[1].split(",");
            List<Integer> values = new java.util.ArrayList<>();
            for (String valueString : valueStrings) {
                values.add(Integer.parseInt(valueString));
            }
            res.put(ruleWeight, values);
        }
        return res;
    }

    public Map<Long, String> getRuleWeightValues() {
        // 根据ruleValue解析出ruleWeight和ruleValues
        //  6000:"6000:102,103,104,105,106,107,108,109"
        //  5000:"5000:101,102,103,104,105,106,107,108,109"
        Map<Long, String> res = new HashMap<>();
        String[] ruleValueGroups = ruleValue.split(" ");
        for (String ruleValueGroup : ruleValueGroups) {
            String[] parts = ruleValueGroup.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueGroup);
            }
            String ruleWeight = parts[0];
            res.put(Long.valueOf(ruleWeight), ruleValueGroup);
        }
        return res;
    }

    public Map<Integer, List<String>> getBlackListUserValues() {
//        100:user001,user002,user003 101:user005
        // 根据ruleValue解析出在黑名单中的用户
        Map<Integer, List<String>> res = new HashMap<>();
        String[] ruleValueGroups = ruleValue.split(" ");
        for (String ruleValueGroup : ruleValueGroups) {
            String[] parts = ruleValueGroup.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_blacklist rule_rule invalid input format" + ruleValueGroup);
            }
            String awardId = parts[0];
            String[] valueStrings = parts[1].split(",");
            List<String> values = new java.util.ArrayList<>();
            for (String valueString : valueStrings) {
                values.add(valueString);
            }
            res.put(Integer.valueOf(awardId), values);
        }
        return res;

    }
}
