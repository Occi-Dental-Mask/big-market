package cn.occi.trigger.job;

import cn.occi.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import cn.occi.domain.strategy.service.IStockHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 更新奖品库存任务；为了不让更新库存的压力打到数据库中，这里采用了redis更新缓存库存，异步队列更新数据库，数据库表最终一致即可。
 * @create 2024-02-09 12:13
 */
@Slf4j
@Component()
public class UpdateAwardStockJob {

    @Resource
    private IStockHandle raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if (null == strategyAwardStockKeyVO) return;
            log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e) {
            log.error("定时任务，更新奖品消耗库存失败", e);
        }
    }

}
