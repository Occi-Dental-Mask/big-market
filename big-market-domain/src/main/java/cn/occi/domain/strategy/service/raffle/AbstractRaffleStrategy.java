package cn.occi.domain.strategy.service.raffle;

import cn.occi.domain.strategy.model.entity.RaffleAwardEntity;
import cn.occi.domain.strategy.model.entity.RaffleFactorEntity;
import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.IRaffleStrategy;
import cn.occi.domain.strategy.service.IStockHandle;
import cn.occi.domain.strategy.service.orm.IStrategyDispatch;
import cn.occi.domain.strategy.service.rule.chain.factory.ChainNodeFactory;
import cn.occi.domain.strategy.service.rule.tree.factory.TreeNodeFactory;
import cn.occi.types.enums.ResponseCode;
import cn.occi.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.swing.tree.TreeNode;


/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy, IStockHandle {


    @Resource
    protected IStrategyRepository repository;
    @Resource
    protected IStrategyDispatch strategyDraw;
    
    @Resource
    protected ChainNodeFactory chainNodeFactory;

    @Resource
    protected TreeNodeFactory treeNodeFactory;

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 策略查询
        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);

        // 3. 抽奖前 - 责任链处理抽奖前规则
        ChainNodeFactory.StrategyAwardVO chainStrategyAwardVO = chainNodeFactory.openLogicChain(strategyId).executeNode(userId, strategyId);
        log.info("抽奖策略计算-责任链 {} {} {} {}", userId, strategyId, chainStrategyAwardVO.getAwardId(), chainStrategyAwardVO.getLogicModel());
        if (!chainStrategyAwardVO.getLogicModel().equals(ChainNodeFactory.LogicModel.RULE_DEFAULT.getCode())) {
            return RaffleAwardEntity.builder()
                    .awardId(chainStrategyAwardVO.getAwardId())
                    .build();
        }

        // 4. 规则树处理抽奖中和抽奖后规则
        // 建立规则树
        TreeNodeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, chainStrategyAwardVO.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());

        // 5. 返回抽奖结果
        return RaffleAwardEntity.builder()
                .awardId(treeStrategyAwardVO.getAwardId())
                .awardConfig(treeStrategyAwardVO.getAwardRuleValue())
                .build();
    }

    protected abstract TreeNodeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);


}
