package cn.occi.test.domain;

import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.orm.IStrategyWarehouse;
import cn.occi.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void test_assembleStrategyConfig() {
        boolean success = strategyWarehouse.assembleStrategyConfig(100001L);
        log.info("测试结果：{}", success);
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
        log.info("测试结果：{} - 奖品ID值", strategyWarehouse.getRandomAwardId(100001L));
    }
}
