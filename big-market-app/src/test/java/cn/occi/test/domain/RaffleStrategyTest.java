package cn.occi.test.domain;

import cn.occi.domain.strategy.model.entity.RaffleAwardEntity;
import cn.occi.domain.strategy.model.entity.RaffleFactorEntity;
import cn.occi.domain.strategy.service.IRaffleStrategy;
import cn.occi.domain.strategy.service.orm.IStrategyWarehouse;
import cn.occi.domain.strategy.service.rule.chain.factory.ChainNodeFactory;
import cn.occi.domain.strategy.service.rule.chain.impl.WeightRuleNode;
import cn.occi.domain.strategy.service.rule.tree.impl.LockTreeNode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private WeightRuleNode ruleWeightLogicChain;
    @Resource
    private LockTreeNode ruleLockLogicTreeNode;
    @Resource
    private IStrategyWarehouse strategyWarehouse;
    
    @Resource
    private ChainNodeFactory chainNodeFactory;

//    @Before
//    public void setUp() {
//        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore", 4050L);
//    }

    @Test
    public void test_performRaffle() throws InterruptedException {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("xiaofuge")
                .strategyId(100006L)
                .build();
        for (int i = 0; i < 2; i++) {
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

            log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
        }

        new CountDownLatch(1).await();

    }

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // 黑名单用户 user001,user002,user003
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }


    @Before
    public void test_strategyArmory() {
        boolean success = strategyWarehouse.assembleStrategyConfig(100006L);
        log.info("测试结果：{}", success);
        // 通过反射 mock 规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4900L);
        ReflectionTestUtils.setField(ruleLockLogicTreeNode, "userRaffleCount", 10L);
    }

    /**
     * 次数错校验，抽奖n次后解锁。100003 策略，你可以通过调整 @Before 的 setUp 方法中个人抽奖次数来验证。比如最开始设置0，之后设置10
     * ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleCount", 10L);
     */
    @Test
    public void test_raffle_center_rule_lock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("xiaofuge")
                .strategyId(100003L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }


}
