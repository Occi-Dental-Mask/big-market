package cn.occi.domain.strategy.service.rule.chain.factory;

import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.domain.strategy.repository.IStrategyRepository;
import cn.occi.domain.strategy.service.rule.chain.IResponseNode;
import lombok.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/3
 */
@Service
public class ChainNodeFactory {
    private final ApplicationContext applicationContext;
    // 仓储信息
    protected IStrategyRepository repository;
    // 存放策略链，策略ID -> 责任链
    private final Map<Long, IResponseNode> strategyChainGroup;

    public ChainNodeFactory(ApplicationContext applicationContext, IStrategyRepository repository) {
        this.applicationContext = applicationContext;
        this.repository = repository;
        this.strategyChainGroup = new ConcurrentHashMap<>();
    }

    /**
     * 通过策略ID，构建责任链
     *
     * @param strategyId 策略ID
     * @return LogicChain
     */
    public IResponseNode openLogicChain(Long strategyId) {
        IResponseNode cacheLogicChain = strategyChainGroup.get(strategyId);
        if (null != cacheLogicChain) return cacheLogicChain;

        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        // 如果未配置策略规则，则只装填一个默认责任链
        if (null == ruleModels || 0 == ruleModels.length) {
            IResponseNode ruleDefaultLogicChain = applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), IResponseNode.class);
            // 写入缓存
            strategyChainGroup.put(strategyId, ruleDefaultLogicChain);
            return ruleDefaultLogicChain;
        }

        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        IResponseNode logicChain = applicationContext.getBean(ruleModels[0], IResponseNode.class);
        IResponseNode current = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            IResponseNode nextChain = applicationContext.getBean(ruleModels[i], IResponseNode.class);
            
            current = current.appendNext(nextChain);
        }

        // 责任链的最后装填默认责任链
        current.appendNext(applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), IResponseNode.class));
        // 写入缓存
        strategyChainGroup.put(strategyId, logicChain);

        return logicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /**
         * 抽奖奖品ID - 内部流转使用
         */
        private Integer awardId;
        /**
         * 逻辑模型
         */
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }
}
