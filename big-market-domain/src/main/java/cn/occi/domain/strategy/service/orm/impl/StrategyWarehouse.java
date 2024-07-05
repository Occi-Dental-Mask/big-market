package cn.occi.domain.strategy.service.orm.impl;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;
import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.orm.IStrategyDraw;
import cn.occi.domain.strategy.service.orm.IStrategyWarehouse;
import cn.occi.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/29
 */
@Service
@Slf4j
public class StrategyWarehouse implements IStrategyWarehouse, IStrategyDraw {

    @Resource
    private IStrategyRepository strategyRepo;


    @Override
    public boolean assembleStrategyConfig(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> awardEntityList = strategyRepo.queryStrategyAwardList(strategyId);
        // 2. 普通策略装配
        assembleStrategyProb(String.valueOf(strategyId), awardEntityList);
        // 3.权重策略装配
        // 查询strategy表，找出rule_weight字段
        // 2. 权重策略配置 - 适用于 rule_weight 权重规则配置
        StrategyEntity strategyEntity = strategyRepo.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) return true;
        // 查询strategy_rule表，找出strategy_id是否有对应的rule_weight策略
        // 如果有，根据rule_weight找出对应的rule_weight_values
        // 6000:102,103,104,105,106,107,108,109 5000:101,102,103,104,105,106,107,108,109
        StrategyRuleEntity ruleEntity = strategyRepo.queryStrategyRule(strategyId, null,"rule_weight");


        Map<String, List<Integer>> ruleWeightValues = ruleEntity.getRuleWeightLists();
        Set<String> keys = ruleWeightValues.keySet();
        for (String key: keys) {
            List<Integer> ruleWeightValue = ruleWeightValues.get(key);

            List<StrategyAwardEntity> awardEntityListClone = new ArrayList<>(awardEntityList);
            // 去除不在ruleWeightValue中的awardId
            awardEntityListClone.removeIf(entity -> !ruleWeightValue.contains(entity.getAwardId()));
            // 装配该种权重策略
            assembleStrategyProb(String.valueOf(strategyId).concat("_").concat(key), awardEntityListClone);
        }
        return true;
    }


    private void assembleStrategyProb(String key, List<StrategyAwardEntity> awardEntityList) {

        //2、流式操作找出最小概率值
        // 别用流式操作了直接找吧
        BigDecimal minAwardRate = awardEntityList.get(0).getAwardRate();
        for (int i = 1; i < awardEntityList.size(); i++) {
            BigDecimal awardRate = awardEntityList.get(i).getAwardRate();
            if (awardRate.compareTo(minAwardRate) < 0) {
                minAwardRate = awardRate;
            }
        }


//        BigDecimal minAwardRate = strategyAwardEntities.stream()
//                .map(StrategyAwardEntity::getAwardRate)
//                .min(BigDecimal::compareTo)
//                .orElse(BigDecimal.ZERO);

        // 判断是否为0
        if (BigDecimal.ZERO.equals(minAwardRate)) {
            return;
        }
        //3、计算概率区间
        BigDecimal total = awardEntityList.stream().map(StrategyAwardEntity::getAwardRate).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interval = total.divide(minAwardRate, 0, RoundingMode.CEILING);
        BigDecimal logVal = BigDecimal.valueOf(Math.ceil(Math.log10(interval.doubleValue())));
        BigDecimal tenInterval = BigDecimal.TEN.pow(logVal.intValue());

        // 4、按照概率添加awardId
        List<Integer> awardIdList = new ArrayList<>();
        for (StrategyAwardEntity awardEntity : awardEntityList) {
            Integer awardId = awardEntity.getAwardId();
            BigDecimal awardRate = awardEntity.getAwardRate();
            for (int i = 0; i < tenInterval.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                // 添加awardId
                awardIdList.add(awardId);
            }
        }
        //5、打乱awardIdList
        Collections.shuffle(awardIdList);
        //6、根据list生成map
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < awardIdList.size(); i++) {
            map.put(i, awardIdList.get(i));
        }
        // 7、将Map存入缓存
        strategyRepo.storeStrategyAwardSearchRateTable(key, awardIdList.size(), map);

    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        Integer rateRange = strategyRepo.getRateRange(String.valueOf(strategyId));
        if (rateRange == null) {
            return null;
        }
        return strategyRepo.getRandomAwardId(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        Integer rateRange = strategyRepo.getRateRange(key);
        if (rateRange == null) {
            //抛出异常
            log.info("错误！！！没有装配概率策略");
        }
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return strategyRepo.getRandomAwardId(key, new SecureRandom().nextInt(rateRange));
    }
}
