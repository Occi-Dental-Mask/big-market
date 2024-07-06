package cn.occi.domain.strategy.service.rule.tree.impl;

import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.orm.IStrategyDispatch;
import cn.occi.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/4
 */
@Component("rule_stock")
@Slf4j
public class StockTreeNode implements ILogicTreeNode {
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public TreeNodeFactory.TreeActionEntity executeNode(String userId, Long strategyId, Integer awardId, String ruleValue) {
        log.info("规则过滤-库存扣减 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        // 进行库存扣减，如果扣减成功则放行，否则接管（注意：是否放行和数据库中的规则树结构有关）
        if (strategyDispatch.deductAwardStock(strategyId, awardId).booleanValue()) {

            // 写入延迟队列，延迟消费更新数据库记录。【在trigger的job；UpdateAwardStockJob 下消费队列，更新数据库记录】
            strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyId)
                    .awardId(awardId)
                    .build());

            return TreeNodeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }
        return TreeNodeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
