package cn.occi.infrastructure.persistent.dao;

import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRuleReq);

}
