package cn.occi.domain.strategy.service.orm.impl;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.orm.IStrategyWarehouse;
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
public class StrategyWarehouse implements IStrategyWarehouse {

    @Resource
    private IStrategyRepository strategyRepo;


    @Override
    public boolean assembleStrategyConfig(Long strategyId) {

        // 1. 查询策略配置
        List<StrategyAwardEntity> awardEntityList = strategyRepo.queryStrategyAwardList(strategyId);


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
            return false;
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
        strategyRepo.storeStrategyAwardSearchRateTable(strategyId, awardIdList.size(), map);

        return true;

    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = strategyRepo.getRateRange(strategyId);
        return strategyRepo.getRandomAwardId(strategyId, new SecureRandom().nextInt(rateRange));
    }
}
