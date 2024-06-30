package cn.occi.infrastructure.persistent.repository;

import cn.occi.domain.strategy.model.entity.StrategyAwardEntity;
import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.model.entity.StrategyRuleEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.occi.infrastructure.persistent.dao.IStrategyDao;
import cn.occi.infrastructure.persistent.dao.IStrategyRuleDao;
import cn.occi.infrastructure.persistent.po.Strategy;
import cn.occi.infrastructure.persistent.po.StrategyAward;
import cn.occi.infrastructure.persistent.po.StrategyRule;
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

    @Resource
    private IStrategyRuleDao strategyRuleDao;


    @Resource
    private IStrategyDao strategyDao;


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
    public void storeStrategyAwardSearchRateTable(String key, int size, Map<Integer, Integer> map) {
        // 存储概率范围值
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, size);
        // 存储概率哈希表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(map);
    }

    @Override
    public Integer getRandomAwardId(String key, int i) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, i);
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        StrategyRule strategyRuleRes = strategyRuleDao.queryStrategyRule(strategyRuleReq);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRuleRes.getStrategyId())
                .awardId(strategyRuleRes.getAwardId())
                .ruleType(strategyRuleRes.getRuleType())
                .ruleModel(strategyRuleRes.getRuleModel())
                .ruleValue(strategyRuleRes.getRuleValue())
                .ruleDesc(strategyRuleRes.getRuleDesc())
                .build();
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 优先从redis中获取数据
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        // 缓存中有数据，直接返回
        if (null != strategyEntity) return strategyEntity;
        // 缓存中没有数据，从数据库中获取
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();        // 将数据存入缓存
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }
}
