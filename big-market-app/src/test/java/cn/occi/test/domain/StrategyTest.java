package cn.occi.test.domain;

import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.orm.IStrategyDraw;
import cn.occi.domain.strategy.service.orm.IStrategyWarehouse;
import cn.occi.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @description:
 * @author: occi
 * @date: 2024/6/29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Resource
    private IStrategyWarehouse strategyWarehouse;

    @Resource
    private IRedisService redisService;

    @Resource
    private IStrategyDraw strategyDraw;
    /**
     * 策略ID；100001L、100002L 装配的时候创建策略表写入到 Redis Map 中
     */
    @Before
    public void test_strategyArmory() {
        boolean success = strategyWarehouse.assembleStrategyConfig(100001L);
        log.info("测试结果：{}", success);
    }
    @Before
    public void test_assembleStrategyConfig() {
//        boolean success = strategyWarehouse.assembleStrategyConfig(100001L);
//        strategyWarehouse.assembleStrategyConfig(100006L);
        for (int i = 1; i <= 6; i++) {
            strategyWarehouse.assembleStrategyConfig(Long.valueOf("10000" + String.valueOf(i)));
        }

        log.info("测试结果：{}", "装配成功!" );
    }

    @Test
    public void test_map() {
        RMap<Integer, Integer> map = redisService.getMap("strategy_id_100001");
        map.put(1, 101);
        map.put(2, 101);
        map.put(3, 101);
        map.put(4, 102);
        map.put(5, 102);
        map.put(6, 102);
        map.put(7, 103);
        map.put(8, 103);
        map.put(9, 104);
        map.put(10, 105);

        log.info("测试结果：{}", redisService.getMap("strategy_id_100001").get(1));
    }

    @Test
    public void test_getAssembleRandomVal() {
        log.info("测试结果：{} - 奖品ID值", strategyDraw.getRandomAwardId(100001L));
    }

    @Test
    public void test_getRandomAwardId_ruleWeightValue() {
        log.info("测试结果：{} - 4000 策略配置", strategyDraw.getRandomAwardId(100001L, "4000"));
        log.info("测试结果：{} - 5000 策略配置", strategyDraw.getRandomAwardId(100001L, "5000"));
        log.info("测试结果：{} - 6000 策略配置", strategyDraw.getRandomAwardId(100001L, "6000"));
    }
}
