package cn.occi.infrastructure.persistent.repository;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.occi.infrastructure.persistent.po.StrategyAward;
import cn.occi.infrastructure.persistent.redis.IRedisService;
import cn.occi.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/29
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IRedisService redisService;

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 1、从缓存中获取
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwards = redisService.getValue(cacheKey);
        if (null != strategyAwards && !strategyAwards.isEmpty()) return strategyAwards;
        // 2、从数据库中获取
        List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwards = new ArrayList<>(strategyAwardList.size());
        for (StrategyAward strategyAward : strategyAwardList) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwards.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwards);
        return strategyAwards;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(Long strategyId, int size, Map<Integer, Integer> map) {
        // 存储概率范围值
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, size);
        // 存储概率哈希表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(map);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, int i) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, i);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }
}
