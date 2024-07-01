package cn.occi.domain.strategy.service.raffle;

import cn.occi.domain.strategy.model.entity.RaffleAwardEntity;
import cn.occi.domain.strategy.model.entity.RaffleFactorEntity;
import cn.occi.domain.strategy.model.entity.RuleActionEntity;
import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.IRaffleStrategy;
import cn.occi.domain.strategy.service.factory.LogicFilterFactory;
import cn.occi.domain.strategy.service.orm.IStrategyDraw;
import cn.occi.types.enums.ResponseCode;
import cn.occi.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/1
 */

public abstract class AbstractRaffleStrategy implements IRaffleStrategy {


    @Resource
    protected IStrategyRepository repository;
    @Resource
    protected IStrategyDraw strategyDraw;

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

        // 3. 抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity =
                this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build(),
                strategy.ruleModels());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())) {
            if (LogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())) {
                // 黑名单返回固定的奖品ID
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
            } else if (LogicFilterFactory.LogicModel.RULE_WEIGHT.getCode().equals(ruleActionEntity.getRuleModel())) {
                // 权重根据返回的信息进行抽奖
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                String ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();
                Integer awardId = strategyDraw.getRandomAwardId(strategyId, ruleWeightValueKey);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        // 4. 默认抽奖流程
        Integer awardId = strategyDraw.getRandomAwardId(strategyId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
